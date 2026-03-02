package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
