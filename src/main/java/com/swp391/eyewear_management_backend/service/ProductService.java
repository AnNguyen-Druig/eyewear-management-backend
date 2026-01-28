package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    public List<ProductResponse> searchProducts(String productName, Double minPrice, Double maxPrice, String brand);

    public ProductDetailResponse getProductById(Long id);
}