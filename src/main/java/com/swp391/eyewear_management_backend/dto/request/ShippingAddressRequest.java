package com.swp391.eyewear_management_backend.dto.request;

import lombok.Data;

@Data
public class ShippingAddressRequest {
    private String street;

    private Integer provinceCode;
    private String provinceName;

    private Integer districtCode;
    private String districtName;

    private String wardCode;
    private String wardName;
}
