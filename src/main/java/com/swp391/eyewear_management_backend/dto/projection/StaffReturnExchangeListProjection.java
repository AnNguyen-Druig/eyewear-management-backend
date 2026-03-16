package com.swp391.eyewear_management_backend.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface StaffReturnExchangeListProjection {
    Long getReturnExchangeId();
    String getReturnCode();
    Long getOrderId();
    String getOrderCode();
    LocalDateTime getOrderDate();
    String getOrderStatus();
    String getCustomerName();
    String getCustomerPhone();
    String getCustomerEmail();
    String getReturnType();
    String getRequestScope();
    LocalDateTime getRequestDate();
    String getReturnExchangeStatus();
    BigDecimal getRefundAmount();
    String getRefundMethod();
    String getRefundAccountNumber();
    String getRefundAccountName();
    String getRequestNote();
    String getRejectReason();
    LocalDateTime getApprovedDate();
    LocalDateTime getProcessedDate();
}
