package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {

    Long contactLensId;
    Long frameId;
    Long lensId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MUST_BE_GREATER_THAN_0")
    Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "FRAME_PRICE_MUST_BE_GREATER_THAN_0")
    Double framePrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "LENS_PRICE_MUST_BE_GREATER_THAN_0")
    Double lensPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_MUST_BE_GREATER_THAN_0")
    Double price;

    // Prescription fields (optional) - liên kết với bảng Cart_Item_Prescription
    @DecimalMin(value = "0.0")
    Double rightEyeSph;
    @DecimalMin(value = "0.0")
    Double rightEyeCyl;
    @DecimalMin(value = "0.0")
    Integer rightEyeAxis;
    @DecimalMin(value = "0.0")
    Double rightEyeAdd;
    @DecimalMin(value = "0.0")
    Double leftEyeSph;
    @DecimalMin(value = "0.0")
    Double leftEyeCyl;
    @DecimalMin(value = "0.0")
    Integer leftEyeAxis;
    @DecimalMin(value = "0.0")
    Double leftEyeAdd;

    // Pupillary Distance
    @DecimalMin(value = "30.0")
    Double pd;
    @DecimalMin(value = "30.0")
    Double pdRight;
    @DecimalMin(value = "30.0")
    Double pdLeft;
}
