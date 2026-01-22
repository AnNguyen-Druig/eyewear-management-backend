package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "show-all-products")
public interface ProductRepo extends JpaRepository<Product, Long> {
}
