package com.swp391.eyewear_management_backend.dto.response.extend;

import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContactLensResponse extends ProductDetailResponse {
    private String Description;
    private String waterContent;
    private String diameter;
    private List<ProductResponse> relatedContactLenses;
}
