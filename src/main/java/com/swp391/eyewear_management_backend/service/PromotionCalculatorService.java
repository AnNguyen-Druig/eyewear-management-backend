package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.response.PromotionCandidateResponse;
import com.swp391.eyewear_management_backend.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionCalculatorService {

    PromotionResult evaluate(List<CartItem> cartItems, BigDecimal subTotal, Long selectedPromotionId);

    record PromotionResult(
            BigDecimal discountAmount,
            Long appliedPromotionId,
            List<PromotionCandidateResponse> availablePromotions,
            PromotionCandidateResponse recommendedPromotion,
            // itemId -> itemDiscount (để tính prescriptionAmount sau discount)
            java.util.Map<Long, BigDecimal> itemDiscountMap
    ) {}
}
