package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutPreviewRequest {

    @NotEmpty(message = "cartItemIds is required")
    private List<Long> cartItemIds;

    private Long promotionId;        // user chọn 1 promotion (optional)
    private String paymentMethod;    // COD / MOMO / VNPAY (optional)

    private ShippingAddressRequest address; // để tích hợp GHN tính ShippingFee, ExpectedDeliveryAt
}
