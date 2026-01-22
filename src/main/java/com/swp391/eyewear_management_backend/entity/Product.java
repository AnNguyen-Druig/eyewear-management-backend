package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Product")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_ID")
    private Long productID;

    @Column(name = "Product_Name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String productName;

    //Luu y coi bang lai de mapping dung table
    @Column(name = "SKU", columnDefinition = "NVARCHAR(50)")
    private String SKU;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_Type_ID", nullable = false)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Brand_ID", nullable = false)
    private Brand brand;

    @Column(name = "Price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "Cost_Price", nullable = false, precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "Allow_Preorder", nullable = false)
    private Boolean allowPreorder = false;

    @Column(name = "Description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductPromotion> promotions;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Frame frame;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Lens lens;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ContactLens contactLens;

    public Product(String productName, ProductType productType, Brand brand, BigDecimal price,
                   BigDecimal costPrice, Boolean allowPreorder, String description) {
        this.productName = productName;
        this.productType = productType;
        this.brand = brand;
        this.price = price;
        this.costPrice = costPrice;
        this.allowPreorder = allowPreorder;
        this.description = description;
    }
}
