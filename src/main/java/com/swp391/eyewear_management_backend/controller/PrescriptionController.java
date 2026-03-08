package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import com.swp391.eyewear_management_backend.dto.response.PrescriptionOcrResponse;
import com.swp391.eyewear_management_backend.service.PrescriptionOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class PrescriptionController {

    private final PrescriptionOcrService prescriptionOcrService;

    @PostMapping(value = "/parse-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PrescriptionOcrResponse> parsePrescriptionImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.<PrescriptionOcrResponse>builder()
                .message("Prescription parsed successfully")
                .result(prescriptionOcrService.parsePrescriptionImage(file))
                .build();
    }
}
