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

    @NotNull(message = "USER_ID_REQUIRED")
    Long userId;

    @NotNull(message = "CART_ID_REQUIRED")
    Long cartId;

    Long productId;
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
    Double rightEyeSph;
    Double rightEyeCyl;
    Integer rightEyeAxis;
    Double rightEyeAdd;

    Double leftEyeSph;
    Double leftEyeCyl;
    Integer leftEyeAxis;
    Double leftEyeAdd;

    // Pupillary Distance
    Double pd;
    Double pdRight;
    Double pdLeft;
}
