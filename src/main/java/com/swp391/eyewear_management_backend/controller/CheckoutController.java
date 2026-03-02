package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.dto.request.CheckoutPreviewRequest;
import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import com.swp391.eyewear_management_backend.dto.response.CheckoutPreviewResponse;
import com.swp391.eyewear_management_backend.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/preview")
    public ApiResponse<CheckoutPreviewResponse> preview(@RequestBody @Valid CheckoutPreviewRequest request) {
        return ApiResponse.<CheckoutPreviewResponse>builder()
                .message("OK")
                .result(checkoutService.preview(request))
                .build();
    }
}