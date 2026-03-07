package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Promotion_Order_Rule")
@Data
@NoArgsConstructor
public class PromotionOrderRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Promotion_Order_Rule_ID")
    private Long promotionOrderRuleID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Promotion_ID", nullable = false, unique = true)
    private Promotion promotion;

    @Column(name = "Min_Order_Total", nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderTotal;
}