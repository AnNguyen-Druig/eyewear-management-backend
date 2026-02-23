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
public class CartItemResponse {

    private Long cartItemId;

    private Long cartId;

    private Long productId;
    private String productName;
    private Double productPrice;

    private Long frameId;
    private String frameName;
    private Double framePrice;

    private Long lensId;
    private String lensName;
    private Double lensPrice;

    private Double rightEyeSph;
    private Double rightEyeCyl;
    private Integer rightEyeAxis;

    private Double leftEyeSph;
    private Double leftEyeCyl;
    private Integer leftEyeAxis;

    private Integer quantity;

    private Double totalPrice;
}
