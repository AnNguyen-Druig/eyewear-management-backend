package com.swp391.eyewear_management_backend.dto.request;

import jakarta.validation.constraints.NotNull;
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

    Long productId;
    Long frameId;
    Long lensId;

    Double rightEyeSph;
    Double rightEyeCyl;
    Integer rightEyeAxis;

    Double leftEyeSph;
    Double leftEyeCyl;
    Integer leftEyeAxis;

    @NotNull(message = "QUANTITY_REQUIRED")
    Integer quantity;
}
