package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.service.PaymentGatewayService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @Override
    public String createPaymentUrl(String method, Long orderId, Long paymentId, BigDecimal amount) {

        String m = method == null ? "" : method.trim().toUpperCase();

        if ("VNPAY".equals(m)) {
            return UriComponentsBuilder
                    .fromUriString("http://localhost:8080/payments/vnpay/mock")
                    .queryParam("orderId", orderId)
                    .queryParam("paymentId", paymentId)
                    .queryParam("amount", amount)
                    .build()
                    .toUriString();
        }

        if ("MOMO".equals(m)) {
            return UriComponentsBuilder
                    .fromUriString("http://localhost:8080/payments/momo/mock")
                    .queryParam("orderId", orderId)
                    .queryParam("paymentId", paymentId)
                    .queryParam("amount", amount)
                    .build()
                    .toUriString();
        }

        if ("COD".equals(m)) return null;

        // MVP: method lạ thì trả null để không crash
        return null;
    }
}