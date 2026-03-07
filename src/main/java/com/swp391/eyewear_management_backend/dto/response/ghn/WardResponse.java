package com.swp391.eyewear_management_backend.dto.response.ghn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WardResponse {

    private String wardCode;
    private String wardName;
    private Integer districtId;
}
