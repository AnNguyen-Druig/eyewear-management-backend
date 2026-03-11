package com.swp391.eyewear_management_backend.exception;

import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";

    /**
     * 1) Lỗi nghiệp vụ chủ động (AppException)
     * - Dùng ErrorCode của bạn để set httpStatus + code + message.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException ex) {
        ErrorCode ec = ex.getErrorCode();
        String message = ex.getMessage() != null ? ex.getMessage() : ec.getMessage();

        ApiResponse body = ApiResponse.builder()
                .code(ec.getCode())
                .message(message)
                .build();

        return ResponseEntity.status(ec.getHttpStatusCode()).body(body);
    }

    /**
     * 2) Không đủ quyền (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorCode ec = ErrorCode.UNAUTHORIZED;

        ApiResponse body = ApiResponse.builder()
                .code(ec.getCode())
                .message(ec.getMessage())
                .build();

        return ResponseEntity.status(ec.getHttpStatusCode()).body(body);
    }

    /**
     * 3) Validate DTO (@Valid) lỗi
     * - Bạn đang dùng kiểu “defaultMessage là key của enum ErrorCode”.
     * - Nếu fieldError null hoặc key sai -> fallback INVALID_KEY.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        ErrorCode ec = ErrorCode.INVALID_KEY;
        Map<String, Object> attrs = null;

        if (fieldError != null) {
            String enumKey = fieldError.getDefaultMessage();
            try {
                ec = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException ignore) {
                log.warn("Unknown validation key: {}", enumKey);
            }
            attrs = extractMinMax(fieldError.getArguments());
        }

        String msg = (attrs != null) ? mapAttribute(ec.getMessage(), attrs) : ec.getMessage();

        ApiResponse body = ApiResponse.builder()
                .code(ec.getCode())
                .message(msg)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 4) Lỗi ràng buộc DB (FK/CK/NOT NULL/unique)
     * - Trả 409 CONFLICT hoặc 400 tuỳ bạn, nhưng 409 hợp lý hơn cho constraint.
     * - message lấy MostSpecificCause để bạn biết constraint nào fail.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException", ex);

        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : "Database constraint violation";

        ApiResponse body = ApiResponse.builder()
                .code(9999)
                .message(detail)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * 5) Catch-all: lỗi hệ thống chưa dự đoán (NPE, IllegalState, Hibernate lỗi...)
     * - Trả 500.
     * - message cho debug: ex.getMessage() (bạn có thể đổi thành message chung khi production)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("Unhandled exception", ex);

        ErrorCode ec = ErrorCode.UNCATEGORIZED_EXCEPTION;

        ApiResponse body = ApiResponse.builder()
                .code(ec.getCode())
                .message(ex.getMessage() != null ? ex.getMessage() : ec.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ---------------- helpers ----------------

    private Map<String, Object> extractMinMax(Object[] args) {
        if (args == null) return null;

        Integer min = null, max = null;
        for (Object a : args) {
            if (a instanceof Integer i) {
                if (min == null) min = i;
                else if (max == null) max = i;
            }
        }
        if (min == null && max == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put(MIN_ATTRIBUTE, min);
        map.put(MAX_ATTRIBUTE, max);
        return map;
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String result = message;

        if (attributes.get(MIN_ATTRIBUTE) != null) {
            result = result.replace("{" + MIN_ATTRIBUTE + "}", String.valueOf(attributes.get(MIN_ATTRIBUTE)));
        }
        if (attributes.get(MAX_ATTRIBUTE) != null) {
            result = result.replace("{" + MAX_ATTRIBUTE + "}", String.valueOf(attributes.get(MAX_ATTRIBUTE)));
        }

        return result;
    }
}
