package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Order_Processing")
@Data
@NoArgsConstructor
public class OrderProcessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_Processing_ID")
    private Long orderProcessingID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Changed_By", nullable = false)
    private User changedBy;

    @Column(name = "Changed_At", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "Note", columnDefinition = "NVARCHAR(255)")
    private String note;

    public OrderProcessing(Order order, User changedBy, LocalDateTime changedAt, String note) {
        this.order = order;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.note = note;
    }
}
