package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.request.CheckoutPreviewRequest;
import com.swp391.eyewear_management_backend.dto.response.CheckoutPreviewResponse;

public interface CheckoutService {
    CheckoutPreviewResponse preview(CheckoutPreviewRequest request);
}
