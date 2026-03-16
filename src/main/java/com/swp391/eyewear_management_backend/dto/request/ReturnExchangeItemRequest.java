package com.swp391.eyewear_management_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnExchangeItemRequest {
    @JsonProperty("order_detail_id")
    Long orderDetailId;

    @JsonProperty("quantity")
    Integer quantity;

    @JsonProperty("item_reason")
    String itemReason;

    @JsonProperty("note")
    String note;
}
