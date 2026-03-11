package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOperationOrderRequest {
    @NotBlank
    private String action;
}
