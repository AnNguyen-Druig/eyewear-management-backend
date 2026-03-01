package com.swp391.eyewear_management_backend.dto.response.ghn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistrictResponse {

    private Integer districtId;
    private String districtName;
    private Integer provinceId;
}
