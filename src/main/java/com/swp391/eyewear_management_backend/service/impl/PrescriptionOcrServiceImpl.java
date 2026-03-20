package com.swp391.eyewear_management_backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.eyewear_management_backend.config.ocr.OcrProperties;
import com.swp391.eyewear_management_backend.dto.response.PrescriptionOcrResponse;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.service.PrescriptionOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PrescriptionOcrServiceImpl implements PrescriptionOcrService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[+-]?\\d+(?:[\\.,]\\d+)?");
    private static final Pattern SIGNED_NUMBER_PATTERN = Pattern.compile("[+-]\\s*\\d+(?:[\\.,]\\d+)?");
    private static final Pattern TWO_NUMBERS_PATTERN = Pattern.compile("([+-]?\\d+(?:[\\.,]\\d+)?)\\s*[\\/\\\\]\\s*([+-]?\\d+(?:[\\.,]\\d+)?)");

    private final OcrProperties ocrProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PrescriptionOcrResponse parsePrescriptionImage(MultipartFile file) {
        validateFile(file);
        String rawText = callOcr(file);
        return parsePrescription(rawText);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (file.getSize() > ocrProperties.getMaxFileSizeBytes()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
    }

    private String callOcr(MultipartFile file) {
        if (!ocrProperties.isEnabled() || !StringUtils.hasText(ocrProperties.getApiKey())) {
            throw new AppException(ErrorCode.PRESCRIPTION_OCR_NOT_CONFIGURED);
        }

        String boundary = "----Boundary" + UUID.randomUUID();
        byte[] body;
        try {
            body = buildMultipartBody(file, boundary);
        } catch (IOException e) {
            throw new AppException(ErrorCode.PRESCRIPTION_OCR_FAILED);
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(ocrProperties.getTimeoutSeconds()))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ocrProperties.getEndpoint()))
                .timeout(Duration.ofSeconds(ocrProperties.getTimeoutSeconds()))
                .header("apikey", ocrProperties.getApiKey())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRESCRIPTION_OCR_FAILED);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AppException(ErrorCode.PRESCRIPTION_OCR_FAILED);
        }

        try {
            JsonNode root = objectMapper.readTree(response.body());
            boolean hasError = root.path("IsErroredOnProcessing").asBoolean(false);
            if (hasError) {
                throw new AppException(ErrorCode.PRESCRIPTION_OCR_FAILED);
            }
            JsonNode parsedResults = root.path("ParsedResults");
            if (!parsedResults.isArray() || parsedResults.isEmpty()) {
                throw new AppException(ErrorCode.PRESCRIPTION_OCR_EMPTY_TEXT);
            }
            StringBuilder text = new StringBuilder();
            for (JsonNode result : parsedResults) {
                String parsedText = result.path("ParsedText").asText("");
                if (StringUtils.hasText(parsedText)) {
                    if (text.length() > 0) {
                        text.append('\n');
                    }
                    text.append(parsedText);
                }
            }
            if (!StringUtils.hasText(text.toString())) {
                throw new AppException(ErrorCode.PRESCRIPTION_OCR_EMPTY_TEXT);
            }
            return text.toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.PRESCRIPTION_OCR_FAILED);
        }
    }

    private byte[] buildMultipartBody(MultipartFile file, String boundary) throws IOException {
        String lineBreak = "\r\n";
        byte[] fileBytes = file.getBytes();
        String filename = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "prescription.jpg";
        String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";

        StringBuilder sb = new StringBuilder();
        appendFormField(sb, boundary, "language", ocrProperties.getLanguage());
        appendFormField(sb, boundary, "isOverlayRequired", "false");
        appendFormField(sb, boundary, "scale", "true");
        appendFormField(sb, boundary, "OCREngine", "2");

        sb.append("--").append(boundary).append(lineBreak);
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"").append(lineBreak);
        sb.append("Content-Type: ").append(contentType).append(lineBreak);
        sb.append(lineBreak);

        byte[] prefix = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] suffix = (lineBreak + "--" + boundary + "--" + lineBreak).getBytes(StandardCharsets.UTF_8);
        byte[] payload = new byte[prefix.length + fileBytes.length + suffix.length];
        System.arraycopy(prefix, 0, payload, 0, prefix.length);
        System.arraycopy(fileBytes, 0, payload, prefix.length, fileBytes.length);
        System.arraycopy(suffix, 0, payload, prefix.length + fileBytes.length, suffix.length);
        return payload;
    }

    private void appendFormField(StringBuilder sb, String boundary, String name, String value) {
        String lineBreak = "\r\n";
        sb.append("--").append(boundary).append(lineBreak);
        sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(lineBreak);
        sb.append(lineBreak);
        sb.append(value).append(lineBreak);
    }

    private PrescriptionOcrResponse parsePrescription(String rawText) {
        List<String> warnings = new ArrayList<>();
        String normalizedText = normalizeText(rawText);

        ParsedEye rightEye = new ParsedEye();
        ParsedEye leftEye = new ParsedEye();

        rightEye.sph = findSignedValueAfterEyeMarker(normalizedText, true, 160, -20, 20);
        leftEye.sph = findSignedValueAfterEyeMarker(normalizedText, false, 160, -20, 20);

        List<String> cylValues = findColumnSignedValuesByKeywords(normalizedText, List.of("cye", "cyl"), 2, 220, -10, 10);
        if (cylValues.size() >= 2) {
            rightEye.cyl = cylValues.get(0);
            leftEye.cyl = cylValues.get(1);
        }

        List<String> axisValues = findColumnUnsignedValuesByKeywords(normalizedText, List.of("axe", "ax"), 2, 220, 0, 180, true);
        if (axisValues.size() >= 2) {
            rightEye.axis = axisValues.get(0);
            leftEye.axis = axisValues.get(1);
        }

        List<String> addValues = findColumnSignedValues(normalizedText, "add", 2, 260, -5, 5);
        if (addValues.isEmpty()) {
            addValues = findColumnUnsignedOpticalValues(normalizedText, "add", 2, 260, 0, 5);
        }
        if (addValues.size() >= 2) {
            rightEye.add = addValues.get(0);
            leftEye.add = addValues.get(1);
        } else if (addValues.size() == 1) {
            rightEye.add = addValues.get(0);
            leftEye.add = addValues.get(0);
        }

        if (!StringUtils.hasText(rightEye.sph) || !StringUtils.hasText(leftEye.sph)) {
            List<String> sphFallback = findColumnSignedValues(normalizedText, "sph", 2, 280, -20, 20);
            if (!StringUtils.hasText(rightEye.sph) && sphFallback.size() >= 1) {
                rightEye.sph = sphFallback.get(0);
            }
            if (!StringUtils.hasText(leftEye.sph) && sphFallback.size() >= 2) {
                leftEye.sph = sphFallback.get(1);
            }
        }

        rightEye.detected = StringUtils.hasText(rightEye.sph) && StringUtils.hasText(rightEye.cyl) && StringUtils.hasText(rightEye.axis);
        leftEye.detected = StringUtils.hasText(leftEye.sph) && StringUtils.hasText(leftEye.cyl) && StringUtils.hasText(leftEye.axis);

        ParsedPd parsedPd = parsePd(normalizedText);

        if (!rightEye.detected) {
            warnings.add("Không nhận diện được đầy đủ mắt phải, cần kiểm tra tay.");
        }
        if (!leftEye.detected) {
            warnings.add("Không nhận diện được đầy đủ mắt trái, cần kiểm tra tay.");
        }
        if (!StringUtils.hasText(parsedPd.pd) && !StringUtils.hasText(parsedPd.pdRight) && !StringUtils.hasText(parsedPd.pdLeft)) {
            warnings.add("Không nhận diện được PD rõ ràng.");
        }

        double confidence = calculateConfidence(rightEye, leftEye, parsedPd, warnings);

        return PrescriptionOcrResponse.builder()
                .rightEyeSph(rightEye.sph)
                .rightEyeCyl(rightEye.cyl)
                .rightEyeAxis(rightEye.axis)
                .rightEyeAdd(rightEye.add)
                .leftEyeSph(leftEye.sph)
                .leftEyeCyl(leftEye.cyl)
                .leftEyeAxis(leftEye.axis)
                .leftEyeAdd(leftEye.add)
                .pd(parsedPd.pd)
                .pdRight(parsedPd.pdRight)
                .pdLeft(parsedPd.pdLeft)
                .confidence(confidence)
                .requiresReview(confidence < 0.85 || !warnings.isEmpty())
                .warnings(warnings)
                .rawText(rawText)
                .build();
    }

    private ParsedPd parsePd(String normalizedText) {
        ParsedPd result = new ParsedPd();
        String pdSegment = extractSegmentAfterKeyword(normalizedText, "pd", 180);
        if (!StringUtils.hasText(pdSegment)) {
            pdSegment = extractSegmentAfterKeyword(normalizedText, "kcdt", 180);
        }
        if (!StringUtils.hasText(pdSegment)) {
            return result;
        }

        Matcher pairMatcher = TWO_NUMBERS_PATTERN.matcher(pdSegment);
        if (pairMatcher.find()) {
            String first = normalizeNumber(pairMatcher.group(1), false, false);
            String second = normalizeNumber(pairMatcher.group(2), false, false);
            Double firstValue = parseDouble(first);
            Double secondValue = parseDouble(second);
            if (firstValue != null && secondValue != null) {
                if (isInRange(firstValue, 20, 45) && isInRange(secondValue, 20, 45)) {
                    result.pdRight = first;
                    result.pdLeft = second;
                    result.pd = trimZeros(BigDecimal.valueOf(firstValue + secondValue));
                    return result;
                }
                if (isInRange(firstValue, 50, 90)) {
                    result.pd = first;
                    return result;
                }
            }
        }

        boolean isHalfPd = normalizedText.contains("pd/2") || normalizedText.contains("pd 2");
        List<String> pdNumbers = findColumnUnsignedValues(normalizedText, "pd", 3, 220, 20, 90, false);
        if (pdNumbers.isEmpty()) {
            pdNumbers = findColumnUnsignedValues(normalizedText, "kcdt", 3, 220, 20, 90, false);
        }
        if (pdNumbers.isEmpty()) {
            return result;
        }

        if (pdNumbers.size() >= 2) {
            Double firstValue = parseDouble(pdNumbers.get(0));
            Double secondValue = parseDouble(pdNumbers.get(1));
            if (firstValue != null && secondValue != null
                    && isInRange(firstValue, 20, 45)
                    && isInRange(secondValue, 20, 45)) {
                result.pdRight = pdNumbers.get(0);
                result.pdLeft = pdNumbers.get(1);
                result.pd = trimZeros(BigDecimal.valueOf(firstValue + secondValue));
                return result;
            }
        }
        if (isHalfPd) {
            Double halfPd = parseDouble(pdNumbers.get(0));
            if (halfPd != null && isInRange(halfPd, 20, 45)) {
                result.pdRight = pdNumbers.get(0);
                result.pdLeft = pdNumbers.get(0);
                result.pd = trimZeros(BigDecimal.valueOf(halfPd * 2));
                return result;
            }
        }
        result.pd = pdNumbers.get(0);
        return result;
    }

    private String findSignedValueAfterEyeMarker(String normalizedText, boolean right, int windowLength, double min, double max) {
        String markerRegex = right
                ? "(?:phai\\s*\\(r\\)|\\bod\\b|mat\\s*phai)"
                : "(?:trai\\s*\\(l\\)|\\bos\\b|mat\\s*trai)";
        Pattern markerPattern = Pattern.compile(markerRegex);
        Matcher markerMatcher = markerPattern.matcher(normalizedText);
        while (markerMatcher.find()) {
            int start = markerMatcher.end();
            int end = Math.min(normalizedText.length(), start + windowLength);
            String window = normalizedText.substring(start, end);
            List<String> signedNumbers = extractSignedNumbers(window);
            for (String candidate : signedNumbers) {
                String normalized = normalizeOpticalSignedValue(candidate, min, max);
                if (StringUtils.hasText(normalized)) {
                    return normalized;
                }
            }
        }
        return null;
    }

    private List<String> findColumnSignedValuesByKeywords(String normalizedText, List<String> keywords, int maxCount, int windowLength,
                                                          double min, double max) {
        for (String keyword : keywords) {
            List<String> values = findColumnSignedValues(normalizedText, keyword, maxCount, windowLength, min, max);
            if (!values.isEmpty()) {
                return values;
            }
        }
        return new ArrayList<>();
    }

    private List<String> findColumnSignedValues(String normalizedText, String keyword, int maxCount, int windowLength, double min, double max) {
        List<String> results = new ArrayList<>();
        String segment = extractSegmentAfterKeyword(normalizedText, keyword, windowLength);
        if (!StringUtils.hasText(segment)) {
            return results;
        }
        List<String> signedNumbers = extractSignedNumbers(segment);
        for (String candidate : signedNumbers) {
            String normalized = normalizeOpticalSignedValue(candidate, min, max);
            if (!StringUtils.hasText(normalized)) {
                continue;
            }
            results.add(normalized);
            if (results.size() >= maxCount) {
                break;
            }
        }
        return results;
    }

    private List<String> findColumnUnsignedValuesByKeywords(String normalizedText, List<String> keywords, int maxCount, int windowLength,
                                                            double min, double max, boolean integerOnly) {
        for (String keyword : keywords) {
            List<String> values = findColumnUnsignedValues(normalizedText, keyword, maxCount, windowLength, min, max, integerOnly);
            if (!values.isEmpty()) {
                return values;
            }
        }
        return new ArrayList<>();
    }

    private List<String> findColumnUnsignedValues(String normalizedText, String keyword, int maxCount, int windowLength,
                                                  double min, double max, boolean integerOnly) {
        List<String> results = new ArrayList<>();
        String segment = extractSegmentAfterKeyword(normalizedText, keyword, windowLength);
        if (!StringUtils.hasText(segment)) {
            return results;
        }
        List<String> numbers = extractNumbers(segment);
        for (String candidate : numbers) {
            String normalized = normalizeNumber(candidate, false, integerOnly);
            Double value = parseDouble(normalized);
            if (value == null || !isInRange(value, min, max)) {
                continue;
            }
            results.add(normalized);
            if (results.size() >= maxCount) {
                break;
            }
        }
        return results;
    }

    private List<String> findColumnUnsignedOpticalValues(String normalizedText, String keyword, int maxCount, int windowLength,
                                                         double min, double max) {
        List<String> results = new ArrayList<>();
        String segment = extractSegmentAfterKeyword(normalizedText, keyword, windowLength);
        if (!StringUtils.hasText(segment)) {
            return results;
        }
        List<String> numbers = extractNumbers(segment);
        for (String candidate : numbers) {
            String normalized = normalizeOpticalUnsignedValue(candidate, min, max);
            if (!StringUtils.hasText(normalized)) {
                continue;
            }
            results.add(normalized);
            if (results.size() >= maxCount) {
                break;
            }
        }
        return results;
    }

    private String extractSegmentAfterKeyword(String normalizedText, String keyword, int windowLength) {
        int keywordIndex = normalizedText.indexOf(keyword);
        if (keywordIndex < 0) {
            return null;
        }
        int start = keywordIndex + keyword.length();
        int end = Math.min(normalizedText.length(), start + windowLength);
        return normalizedText.substring(start, end);
    }

    private List<String> extractSignedNumbers(String value) {
        List<String> numbers = new ArrayList<>();
        Matcher matcher = SIGNED_NUMBER_PATTERN.matcher(value);
        while (matcher.find()) {
            numbers.add(matcher.group().replace(" ", ""));
        }
        return numbers;
    }

    private List<String> extractNumbers(String value) {
        List<String> numbers = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(value);
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        return numbers;
    }

    private String normalizeText(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}+", "");
        normalized = normalized.replace('\u00A0', ' ');
        normalized = normalized.toLowerCase(Locale.ROOT);
        normalized = normalized.replace('|', ' ');
        normalized = normalized.replace('\r', ' ');
        normalized = normalized.replace('\n', ' ');
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    private String normalizeNumber(String value, boolean allowSign, boolean integerOnly) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.replace(',', '.').trim();
        if (!allowSign) {
            normalized = normalized.replace("+", "");
        }
        try {
            BigDecimal bd = new BigDecimal(normalized);
            if (integerOnly) {
                return String.valueOf(bd.setScale(0, RoundingMode.HALF_UP).intValue());
            }
            return trimZeros(bd);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeOpticalSignedValue(String value, double min, double max) {
        String normalized = normalizeNumber(value, true, false);
        Double parsed = parseDouble(normalized);
        if (parsed == null) {
            return null;
        }
        if (isInRange(parsed, min, max)) {
            return normalized;
        }
        if (Math.abs(parsed) >= 50 && Math.abs(parsed) <= 3000) {
            double scaled = parsed / 100.0;
            if (isInRange(scaled, min, max)) {
                return trimZeros(BigDecimal.valueOf(scaled));
            }
        }
        return null;
    }

    private String normalizeOpticalUnsignedValue(String value, double min, double max) {
        String normalized = normalizeNumber(value, false, false);
        Double parsed = parseDouble(normalized);
        if (parsed == null) {
            return null;
        }
        if (isInRange(parsed, min, max)) {
            return normalized;
        }
        if (Math.abs(parsed) >= 50 && Math.abs(parsed) <= 3000) {
            double scaled = parsed / 100.0;
            if (isInRange(scaled, min, max)) {
                return trimZeros(BigDecimal.valueOf(scaled));
            }
        }
        return null;
    }

    private Double parseDouble(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return new BigDecimal(value).doubleValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    private String trimZeros(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private double calculateConfidence(ParsedEye rightEye, ParsedEye leftEye, ParsedPd pd, List<String> warnings) {
        double score = 0.25;
        score += eyeScore(rightEye);
        score += eyeScore(leftEye);
        if (StringUtils.hasText(pd.pd) || (StringUtils.hasText(pd.pdRight) && StringUtils.hasText(pd.pdLeft))) {
            score += 0.15;
        }
        if (!warnings.isEmpty()) {
            score -= 0.10;
        }
        if (score < 0.0) {
            return 0.0;
        }
        if (score > 1.0) {
            return 1.0;
        }
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double eyeScore(ParsedEye eye) {
        double score = 0.0;
        if (eye.detected) {
            score += 0.15;
        }
        if (StringUtils.hasText(eye.sph)) {
            score += 0.10;
        }
        if (StringUtils.hasText(eye.cyl)) {
            score += 0.10;
        }
        if (StringUtils.hasText(eye.axis)) {
            score += 0.10;
        }
        if (StringUtils.hasText(eye.add)) {
            score += 0.05;
        }
        return score;
    }

    private static class ParsedEye {
        private String sph;
        private String cyl;
        private String axis;
        private String add;
        private boolean detected;
    }

    private static class ParsedPd {
        private String pd;
        private String pdRight;
        private String pdLeft;
    }
}
