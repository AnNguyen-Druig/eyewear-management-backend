package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
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

    @Column(name = "Payment_Date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "Payment_Method", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String paymentMethod;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "Status", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String status;

    public Payment(Order order, LocalDateTime paymentDate, String paymentMethod, BigDecimal amount, String status) {
        this.order = order;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
    }
}
