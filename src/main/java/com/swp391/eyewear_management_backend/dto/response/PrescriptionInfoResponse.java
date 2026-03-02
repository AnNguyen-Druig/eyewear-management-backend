package com.swp391.eyewear_management_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrescriptionInfoResponse {

    private Double rightEyeSph;
    private Double rightEyeCyl;
    private Integer rightEyeAxis;
    private Double rightEyeAdd;

    private Double leftEyeSph;
    private Double leftEyeCyl;
    private Integer leftEyeAxis;
    private Double leftEyeAdd;

    private Double pd;
    private Double pdRight;
    private Double pdLeft;
}