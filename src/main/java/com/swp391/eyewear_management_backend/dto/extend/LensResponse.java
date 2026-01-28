package com.swp391.eyewear_management_backend.dto.extend;

import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LensResponse extends ProductDetailResponse {
    private String Description;
    private String indexValue; // Chiết suất
}
