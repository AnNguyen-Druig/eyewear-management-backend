package com.swp391.eyewear_management_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private Double price;
    private Boolean allowPreorder;
    private Boolean isActive;
    private String Image_URL;
    private String Brand;
    private String Product_Type;
    private Long frameId;
    private Long lensId;
    private Long contactLensId;
    // Thêm trường availableQuantity
    private Integer availableQuantity;

    @JsonProperty("Frame_Shape_Name")
    private String frameShapeName;

    @JsonProperty("Frame_Material_Name")
    private String frameMaterialName;

    @JsonProperty("Color")
    private String color;

    @JsonProperty("Is_Blue_Light_Block")
    private Boolean isBlueLightBlock;

    @JsonProperty("Is_Photochromic")
    private Boolean isPhotochromic;

    @JsonProperty("Type_Name")
    private String typeName;

    @JsonProperty("Usage_Type")
    private String usageType;

    @JsonProperty("Lens_Material")
    private String lensMaterial;

    @JsonProperty("Replacement_Schedule")
    private String replacementSchedule;
}
