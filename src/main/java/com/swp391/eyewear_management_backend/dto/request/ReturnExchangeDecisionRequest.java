package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnExchangeDecisionRequest {
    @NotBlank
    private String action;
    private String rejectReason;
}
