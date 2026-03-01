package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDefaultAddressRequest {

    // Người dùng nhập: "Tên đường/Số nhà"
    @NotBlank(message = "Street is required")
    private String street;

    // Province
    @NotNull(message = "Province code is required")
    private Integer provinceCode;

    @NotBlank(message = "Province name is required")
    private String provinceName;

    // District
    @NotNull(message = "District code is required")
    private Integer districtCode;

    @NotBlank(message = "District name is required")
    private String districtName;

    // Ward (GHN ward code là string)
    @NotBlank(message = "Ward code is required")
    private String wardCode;

    @NotBlank(message = "Ward name is required")
    private String wardName;
}