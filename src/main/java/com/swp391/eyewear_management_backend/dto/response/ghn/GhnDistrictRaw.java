package com.swp391.eyewear_management_backend.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GhnDistrictRaw {
    @JsonProperty("DistrictID")
    private Integer districtId;

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("ProvinceID")
    private Integer provinceId;
}
