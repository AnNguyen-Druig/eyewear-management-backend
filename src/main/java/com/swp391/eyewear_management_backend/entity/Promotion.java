package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Promotion")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"orderPromotions", "productPromotions"})
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Promotion_ID")
    private Long promotionID;

    @Column(name = "Promotion_Code", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String promotionCode;

    @Column(name = "Promotion_Name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String promotionName;

    @Column(name = "Promotion_Type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String promotionType;

    @Column(name = "Discount_Value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "Discount_Type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String discountType;

    @Column(name = "Start_Date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "End_Date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "Usage_Limit")
    private Integer usageLimit;

    @Column(name = "Used_Count", nullable = false)
    private Integer usedCount;

    @Column(name = "Is_Active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderPromotion> orderPromotions;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductPromotion> productPromotions;

    public Promotion(String promotionCode, String promotionName, String promotionType, BigDecimal discountValue,
                     String discountType, LocalDateTime startDate, LocalDateTime endDate, Integer usageLimit,
                     Integer usedCount, Boolean isActive) {
        this.promotionCode = promotionCode;
        this.promotionName = promotionName;
        this.promotionType = promotionType;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.isActive = isActive;
    }
}
