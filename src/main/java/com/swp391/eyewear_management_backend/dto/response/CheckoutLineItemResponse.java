package com.swp391.eyewear_management_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutLineItemResponse {
    private Long cartItemId;

    // DIRECT / PRE_ORDER / PRESCRIPTION
    private String itemType;

    private String name;

    private Integer quantity;
    private BigDecimal unitPrice;  // giá 1 item (CartItem.price)
    private BigDecimal lineTotal;  // unitPrice * qty
    private BigDecimal lineDiscount; // để FE hiển thị nếu muốn (optional)

    // ids để debug/trace
    private Long contactLensId;
    private Long frameId;
    private Long lensId;
}