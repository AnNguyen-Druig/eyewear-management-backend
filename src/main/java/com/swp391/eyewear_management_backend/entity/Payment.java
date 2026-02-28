package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
@Builder
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payment_ID")
    private Long paymentID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @Column(name = "Payment_Purpose", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String paymentPurpose; // DEPOSIT / FULL / REMAINING

    @Column(name = "Created_At", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Payment_Date")
    private LocalDateTime paymentDate; // nullable

    @Column(name = "Payment_Method", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String paymentMethod; // COD / MOMO / VNPAY

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "Status", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String status; // PENDING / SUCCESS / FAILED / REFUNDED

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}