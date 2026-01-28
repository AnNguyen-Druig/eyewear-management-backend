package com.swp391.eyewear_management_backend.dto.extend;

import com.swp391.eyewear_management_backend.dto.ProductDetailResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FrameResponse extends ProductDetailResponse {
    private String Description;
    private String color;
    private String material;
    private String frameShape;
}
