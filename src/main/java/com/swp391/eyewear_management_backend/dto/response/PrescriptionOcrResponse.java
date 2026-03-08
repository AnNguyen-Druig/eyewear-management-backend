package com.swp391.eyewear_management_backend.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionOcrResponse {
    String rightEyeSph;
    String rightEyeCyl;
    String rightEyeAxis;
    String rightEyeAdd;
    String leftEyeSph;
    String leftEyeCyl;
    String leftEyeAxis;
    String leftEyeAdd;
    String pd;
    String pdRight;
    String pdLeft;
    double confidence;
    boolean requiresReview;
    List<String> warnings;
    String rawText;
}
