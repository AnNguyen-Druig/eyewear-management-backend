package com.swp391.eyewear_management_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String productName;
    private Double price;
    private Boolean allowPreorder;
    private String image;
}
