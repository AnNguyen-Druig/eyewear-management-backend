package com.swp391.eyewear_management_backend.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.swp391.eyewear_management_backend.dto.extend.ContactLensResponse;
import com.swp391.eyewear_management_backend.dto.extend.FrameResponse;
import com.swp391.eyewear_management_backend.dto.extend.LensResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data

public abstract class ProductDetailResponse {
    private String Product_Type;
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String brandName;
    private List<String> imageUrls;
}
