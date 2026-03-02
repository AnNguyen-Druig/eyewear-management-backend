package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.request.CreateOrderRequest;
import com.swp391.eyewear_management_backend.dto.response.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
