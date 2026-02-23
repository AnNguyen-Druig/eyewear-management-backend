package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "cart-items")
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartCartId(Long cartId);
}
