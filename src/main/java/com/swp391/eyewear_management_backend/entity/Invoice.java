package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
@Data
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Invoice_ID")
    private Long invoiceID;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false, unique = true)
    private Order order;

    @Column(name = "Issue_Date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "Total_Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "Status", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String status;

    public Invoice(Order order, LocalDateTime issueDate, BigDecimal totalAmount, String status) {
        this.order = order;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
