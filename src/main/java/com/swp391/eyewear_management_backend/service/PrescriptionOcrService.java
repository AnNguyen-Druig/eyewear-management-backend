package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.response.PrescriptionOcrResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PrescriptionOcrService {
    PrescriptionOcrResponse parsePrescriptionImage(MultipartFile file);
}
