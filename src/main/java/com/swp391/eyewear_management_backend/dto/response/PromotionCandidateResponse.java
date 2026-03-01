package com.swp391.eyewear_management_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PromotionCandidateResponse {
    private Long promotionId;
    private String code;
    private String name;

    private String scope; // ORDER / PRODUCT
    private String discountType; // PERCENT / AMOUNT
    private BigDecimal discountValue;
    private BigDecimal maxDiscountValue;

    // discount ước tính áp lên cart hiện tại
    private BigDecimal estimatedDiscount;

    private LocalDateTime endDate;
    private String description;
}