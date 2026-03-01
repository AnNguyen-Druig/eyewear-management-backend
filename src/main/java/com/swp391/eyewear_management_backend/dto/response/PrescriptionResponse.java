package com.swp391.eyewear_management_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrescriptionResponse {

    // Left Eye
    private String leftSPH;
    private String leftCYL;
    private String leftAXIS;
    private String leftADD;
    private String leftPD;

    // Right Eye
    private String rightSPH;
    private String rightCYL;
    private String rightAXIS;
    private String rightADD;
    private String rightPD;
}
