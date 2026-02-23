package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "carts")
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserUserId(Long userId);
}
