package com.swp391.eyewear_management_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Boolean allowPreorder;
    private String Image_URL;
    private String Brand;
    private String Product_Type;
}
