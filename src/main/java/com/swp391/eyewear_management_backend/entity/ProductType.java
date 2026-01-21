package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Product_Type")
@Data
@NoArgsConstructor
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Type_ID")
    private Long productTypeID;

    @Column(name = "Type_Name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String typeName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @OneToMany(mappedBy = "productType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    public ProductType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
    }
}
