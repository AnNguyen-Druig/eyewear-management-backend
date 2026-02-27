package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Product_Promotion")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"product", "promotion"})
public class ProductPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Promotion_ID")
    private Long productPromotionID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Promotion_ID", nullable = false)
    private Promotion promotion;

    @Column(name = "Discount_Value", precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "Start_Date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "End_Date", nullable = false)
    private LocalDateTime endDate;

    public ProductPromotion(Product product, Promotion promotion, BigDecimal discountValue,
                            LocalDateTime startDate, LocalDateTime endDate) {
        this.product = product;
        this.promotion = promotion;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
