package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.ContactLensResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.FrameResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.LensResponse;
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

    @Override
    public ProductDetailResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // MapStruct tự động chọn trả về FrameResponse hay LensResponse
        ProductDetailResponse response = productMapper.toDetailResponse(product);
        
        // Populate related products based on type
        if (response instanceof FrameResponse) {
            FrameResponse frameResponse = (FrameResponse) response;
            populateRelatedProducts(frameResponse, product.getProductID());
        } else if (response instanceof LensResponse) {
            LensResponse lensResponse = (LensResponse) response;
            populateRelatedProducts(lensResponse, product.getProductID());
        } else if (response instanceof ContactLensResponse) {
            ContactLensResponse contactLensResponse = (ContactLensResponse) response;
            populateRelatedProducts(contactLensResponse, product.getProductID());
        }
        
        return response;
    }
    
    private void populateRelatedProducts(FrameResponse response, Long currentProductId) {
        // Lấy 4 gọng khác
        List<Product> relatedFrames = productRepository.findByProductTypeNameExcludingId("Gọng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedFrames(relatedFrames.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
        
        // Lấy 4 tròng kính
        List<Product> relatedLenses = productRepository.findByProductTypeNameExcludingId("Tròng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedLenses(relatedLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }
    
    private void populateRelatedProducts(LensResponse response, Long currentProductId) {
        // Lấy 4 tròng kính khác
        List<Product> relatedLenses = productRepository.findByProductTypeNameExcludingId("Tròng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedLenses(relatedLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
        
        // Lấy 4 gọng kính
        List<Product> relatedFrames = productRepository.findByProductTypeNameExcludingId("Gọng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedFrames(relatedFrames.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }
    
    private void populateRelatedProducts(ContactLensResponse response, Long currentProductId) {
        // Lấy 4 kính áp tròng khác
        List<Product> relatedContactLenses = productRepository.findByProductTypeNameExcludingId("Kính áp tròng", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedContactLenses(relatedContactLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }

}
