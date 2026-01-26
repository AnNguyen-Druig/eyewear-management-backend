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
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "product_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FrameResponse.class, name = "FRAME"),
        @JsonSubTypes.Type(value = LensResponse.class, name = "LENS"),
        @JsonSubTypes.Type(value = ContactLensResponse.class, name = "CONTACT_LENS")
})

public abstract class ProductDetailResponse {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String brandName;
    private List<String> imageUrls;
}
