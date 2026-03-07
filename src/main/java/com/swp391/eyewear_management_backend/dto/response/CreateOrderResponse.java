package com.swp391.eyewear_management_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateOrderResponse {
    private Long orderId;
    private String orderCode;
    private String orderStatus;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private LocalDateTime expectedDeliveryAt;
    private BigDecimal totalAmount;

    private boolean depositRequired;
    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;

    private Long appliedPromotionId;

    // payment info for FE redirect
    private boolean paymentRedirectRequired;
    private String paymentUrl;
    private Long paymentId; // payment record created for online
}