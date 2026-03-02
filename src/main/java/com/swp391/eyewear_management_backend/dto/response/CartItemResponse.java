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

    private Long contactLensId;
    private String contactLensName;
    private Double contactLensPrice;
    private String contactLensImg;

    private Long frameId;
    private String frameName;
    private Double framePrice;
    private String frameImg;

    private Long lensId;
    private String lensName;
    private Double lensPrice;
    private String lensImg;

    private Integer quantity;


    private Double price;

    // Prescription information
    private PrescriptionResponse prescription;
}
