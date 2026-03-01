package com.swp391.eyewear_management_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CheckoutPreviewResponse {

    private List<CheckoutLineItemResponse> items;

    private String orderType; // DIRECT_ORDER / PRE_ORDER / PRESCRIPTION_ORDER / MIX_ORDER

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private LocalDateTime expectedDeliveryAt;
    private BigDecimal totalAmount;

    private boolean depositRequired;
    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;

    // Promotions
    private List<PromotionCandidateResponse> availablePromotions;
    private PromotionCandidateResponse recommendedPromotion;

    // promotion đang áp dụng (nếu user chọn)
    private Long appliedPromotionId;
}