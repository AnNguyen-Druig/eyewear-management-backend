package com.swp391.eyewear_management_backend.dto.projection;

public interface ReturnExchangeOrderSummaryProjection {
    Long getOrderId();
    Long getReturnExchangeId();
    String getStatus();
    String getReturnType();
}
