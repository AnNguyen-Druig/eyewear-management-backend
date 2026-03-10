package com.swp391.eyewear_management_backend.dto.request;

import lombok.Data;

@Data
public class ProductCreateRequest {
    private String sku;
    private String name;
    private Double price;
    private Double costPrice;
    private String description;
    private Boolean allowPreorder = false;
    private Boolean isActive = true;

    // Nhận tên thay vì ID
    private String brandName;
    private String typeName;
}
