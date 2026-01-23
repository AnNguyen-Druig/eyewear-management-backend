package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "show-all-products")
public interface ProductRepo extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.productName LIKE %:name%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:category IS NULL OR p.brand.brandName LIKE %:category%)")
    List<Product> searchProducts(@Param("name") String productName,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("category") String brand);
}
