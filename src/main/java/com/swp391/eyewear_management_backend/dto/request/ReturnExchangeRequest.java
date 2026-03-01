package com.swp391.eyewear_management_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnExchangeRequest {
    
    @JsonProperty("order_detail_id")
    Long orderDetailId;
    
    @JsonProperty("quantity")
    Integer quantity;
    
    @JsonProperty("return_reason")
    String returnReason;
    
    @JsonProperty("product_condition")
    String productCondition;
    
    @JsonProperty("refund_amount")
    BigDecimal refundAmount;
    
    @JsonProperty("refund_method")
    String refundMethod;
    
    @JsonProperty("refund_account_number")
    String refundAccountNumber;
    
    @JsonProperty("image_url")
    String imageUrl;
}
