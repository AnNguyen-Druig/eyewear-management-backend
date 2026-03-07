package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.request.ShippingAddressRequest;
import com.swp391.eyewear_management_backend.entity.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface GhnShippingService {
    ShippingResult calculate(List<CartItem> cartItems, ShippingAddressRequest address);

    record ShippingResult(BigDecimal shippingFee, LocalDateTime expectedDeliveryAt) {}
}
