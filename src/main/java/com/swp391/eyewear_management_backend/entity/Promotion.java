package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Promotion")
@Builder
@Data
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Promotion_ID")
    private Long promotionID;

    @Column(name = "Promotion_Code", nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String promotionCode;

    @Column(name = "Promotion_Name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String promotionName;

    @Column(name = "Promotion_Scope", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String promotionScope; // ORDER / PRODUCT

    @Column(name = "Discount_Type", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String discountType; // PERCENT / AMOUNT

    @Column(name = "Discount_Value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "Max_Discount_Value", precision = 15, scale = 2)
    private BigDecimal maxDiscountValue; // nullable

    @Column(name = "Start_Date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "End_Date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "Usage_Limit")
    private Integer usageLimit;

    @Column(name = "Used_Count", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "Is_Active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "Description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @OneToOne(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PromotionOrderRule orderRule; // chỉ có nếu scope=ORDER

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PromotionProductTarget> productTargets; // chỉ có nếu scope=PRODUCT
}