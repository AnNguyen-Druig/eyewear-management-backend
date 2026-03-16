package com.swp391.eyewear_management_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnExchangeResponse {
    
    @JsonProperty("return_exchange_id")
    Long returnExchangeID;
    
    @JsonProperty("return_code")
    String returnCode;
    
    @JsonProperty("order_id")
    Long orderId;
    
    @JsonProperty("user_id")
    Long userId;
    
    @JsonProperty("return_type")
    String returnType;

    @JsonProperty("request_scope")
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

    @JsonProperty("status")
    String status;
    
    @JsonProperty("request_date")
    LocalDateTime requestDate;
    
    @JsonProperty("approved_date")
    LocalDateTime approvedDate;
    
    @JsonProperty("approved_by_id")
    Long approvedById;
    
    @JsonProperty("processed_date")
    LocalDateTime processedDate;

    @JsonProperty("processed_by_id")
    Long processedById;

    @JsonProperty("reject_reason")
    String rejectReason;
}
