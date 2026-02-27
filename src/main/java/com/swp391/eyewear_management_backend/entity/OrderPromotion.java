package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "Order_Promotion")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"order", "promotion"})
public class OrderPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_Promotion_ID")
    private Long orderPromotionID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Promotion_ID", nullable = false)
    private Promotion promotion;

    @Column(name = "Discount_Value", precision = 15, scale = 2)
    private BigDecimal discountValue;

    public OrderPromotion(Order order, Promotion promotion, BigDecimal discountValue) {
        this.order = order;
        this.promotion = promotion;
        this.discountValue = discountValue;
    }
}
