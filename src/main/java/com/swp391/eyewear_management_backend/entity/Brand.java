package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "Brand")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"brandSuppliers", "products"})
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Brand_ID")
    private Long brandID;

    @Column(name = "Brand_Name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String brandName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "Logo_URL", columnDefinition = "VARCHAR(MAX)")
    private String logoUrl;

    @Column(name = "Status", nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BrandSupplier> brandSuppliers;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    public Brand(String brandName, String description, String logoUrl, Boolean status) {
        this.brandName = brandName;
        this.description = description;
        this.logoUrl = logoUrl;
        this.status = status;
    }
}
