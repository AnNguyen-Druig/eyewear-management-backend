package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
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

    @PrePersist
    public void prePersist() {
        if (issueDate == null) issueDate = LocalDateTime.now();
    }
}