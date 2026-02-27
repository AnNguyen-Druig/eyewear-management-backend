package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.CartItemPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemPrescriptionRepository extends JpaRepository<CartItemPrescription, Long> {
}
