package com.swp391.eyewear_management_backend.service;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String createPaymentUrl(String method, Long orderId, Long paymentId, BigDecimal amount);
}
