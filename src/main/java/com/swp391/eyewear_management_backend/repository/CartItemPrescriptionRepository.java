package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.CartItem;
import com.swp391.eyewear_management_backend.entity.CartItemPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemPrescriptionRepository extends JpaRepository<CartItemPrescription, Long> {
    Optional<CartItemPrescription> findByCartItem(CartItem cartItem);
    List<CartItemPrescription> findByCartItem_CartItemIdIn(List<Long> cartItemIds);
}
