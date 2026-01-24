package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "[Order]")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_ID")
    private Long orderID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name = "Order_Code", unique = true, columnDefinition = "NVARCHAR(50)")
    private String orderCode;

    @Column(name = "Order_Date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "Sub_Total", nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "Tax_Amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "Discount_Amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Formula("(Sub_Total + ISNULL(Tax_Amount, 0) - ISNULL(Discount_Amount, 0))")
    private BigDecimal totalAmount;

    @Column(name = "Order_Type", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String orderType;

    @Column(name = "Order_Status", nullable = false, columnDefinition = "NVARCHAR(30)")
    private String orderStatus;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Invoice invoice;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderProcessing> orderProcessings;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderPromotion> orderPromotions;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PrescriptionOrder prescriptionOrder;

    public Order(User user, String orderCode, LocalDateTime orderDate, BigDecimal subTotal,
                 BigDecimal taxAmount, BigDecimal discountAmount, String orderType, String orderStatus) {
        this.user = user;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.subTotal = subTotal;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
    }
}
