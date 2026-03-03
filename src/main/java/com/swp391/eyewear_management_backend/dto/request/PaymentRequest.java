package com.swp391.eyewear_management_backend.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long userId;
    private String fullName;
    private String phone;
    private String address;
    private int totalAmount;
}