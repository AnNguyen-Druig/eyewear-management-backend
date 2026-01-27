package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import com.swp391.eyewear_management_backend.entity.Product;
import com.swp391.eyewear_management_backend.mapper.ProductMapper;
import com.swp391.eyewear_management_backend.repository.ProductRepo;
import com.swp391.eyewear_management_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductResponse> searchProducts(String name, Double minPrice, Double maxPrice, String brand) {
        List<Product> products = productRepository.searchProducts(name, minPrice, maxPrice, brand);
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
