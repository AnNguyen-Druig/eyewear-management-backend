package com.swp391.eyewear_management_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnExchangeRequest {

    @JsonProperty("order_id")
    // NOT NULL trong DB
    @NotNull(message = "Mã đơn hàng không được để trống")
    Long orderId;

    @JsonProperty("order_detail_id")
    Long orderDetailId;

    @JsonProperty("return_type")
    // NOT NULL trong DB
    @NotBlank(message = "Loại yêu cầu đổi trả (Return Type) không được để trống")
    String returnType;

    @JsonProperty("request_scope")
    // NOT NULL trong DB
    @NotBlank(message = "Phạm vi yêu cầu (Request Scope) không được để trống")
    String requestScope;

    @JsonProperty("request_note")
    String requestNote;

    @JsonProperty("return_reason")
    String returnReason;

    @JsonProperty("customer_evidence_url")
    String customerEvidenceUrl;

    @JsonProperty("refund_amount")
    BigDecimal refundAmount;

    @JsonProperty("refund_method")
    String refundMethod;

    @JsonProperty("refund_account_number")
    String refundAccountNumber;

    @JsonProperty("refund_account_name")
    String refundAccountName;

    @JsonProperty("refund_reference_code")
    String refundReferenceCode;

    @JsonProperty("staff_refund_evidence_url")
    String staffRefundEvidenceUrl;

    @JsonProperty("items")
    List<ReturnExchangeItemRequest> items;
}

