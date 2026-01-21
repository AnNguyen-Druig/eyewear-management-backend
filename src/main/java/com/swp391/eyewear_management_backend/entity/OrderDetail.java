package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Order_Detail")
@Data
@NoArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_Detail_ID")
    private Long orderDetailID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false)
    private Product product;

    @Column(name = "Unit_Price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "Note", columnDefinition = "NVARCHAR(500)")
    private String note;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @OneToOne(mappedBy = "orderDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ReturnExchange returnExchange;

    public OrderDetail(Order order, Product product, BigDecimal unitPrice, String note, Integer quantity) {
        this.order = order;
        this.product = product;
        this.unitPrice = unitPrice;
        this.note = note;
        this.quantity = quantity;
    }
}
