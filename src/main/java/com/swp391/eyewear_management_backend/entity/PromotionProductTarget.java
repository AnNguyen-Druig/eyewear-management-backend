package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Promotion_Product_Target")
@Data
@NoArgsConstructor
public class PromotionProductTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Promotion_Product_Target_ID")
    private Long promotionProductTargetID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Promotion_ID", nullable = false)
    private Promotion promotion;

    @Column(name = "Target_Type", nullable = false, columnDefinition = "NVARCHAR(30)")
    private String targetType; // PRODUCT / PRODUCT_TYPE / BRAND / BRAND_PRODUCT_TYPE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product_ID")
    private Product product; // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product_Type_ID")
    private ProductType productType; // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Brand_ID")
    private Brand brand; // nullable
}