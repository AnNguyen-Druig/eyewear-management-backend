package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Brand_Supplier")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"brand", "supplier"})
public class BrandSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Brand_Supplier_ID")
    private Long brandSupplierID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Brand_ID", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Supplier_ID", nullable = false)
    private Supplier supplier;

    public BrandSupplier(Brand brand, Supplier supplier) {
        this.brand = brand;
        this.supplier = supplier;
    }
}
