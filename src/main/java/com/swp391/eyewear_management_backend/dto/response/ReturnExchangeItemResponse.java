package com.swp391.eyewear_management_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnExchangeItemResponse {
    private Long returnExchangeItemId;
    private Long orderDetailId;
    private Long productId;
    private String productName;
    private Integer requestedQuantity;
    private Integer orderQuantity;
    private String itemReason;
    private String note;
}
