package com.swp391.eyewear_management_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffReturnExchangeListResponse {
    private Long returnExchangeId;
    private String returnCode;
    private Long orderId;
    private String orderCode;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String returnType;
    private String requestScope;
    private LocalDateTime requestDate;
    private String returnExchangeStatus;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String refundAccountNumber;
    private String refundAccountName;
    private String requestNote;
    private String rejectReason;
    private LocalDateTime approvedDate;
    private LocalDateTime processedDate;
}
