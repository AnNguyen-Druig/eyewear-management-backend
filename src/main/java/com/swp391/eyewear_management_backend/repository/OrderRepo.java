package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
}
