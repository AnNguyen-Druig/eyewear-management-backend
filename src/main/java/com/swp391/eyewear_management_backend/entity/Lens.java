package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Lens")
@Data
@NoArgsConstructor
public class Lens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Lens_ID")
    private Long lensID;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false, unique = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Lens_Type_ID", nullable = false)
    private LensType lensType;

    @Column(name = "Index_Value", precision = 5, scale = 2)
    private BigDecimal indexValue;

    @Column(name = "Diameter", precision = 5, scale = 2)
    private BigDecimal diameter;

    @Column(name = "Available_Power_Range", columnDefinition = "NVARCHAR(200)")
    private String availablePowerRange;

    @Column(name = "Is_Blue_Light_Block")
    private Boolean isBlueLightBlock;

    @Column(name = "Is_Photochromic")
    private Boolean isPhotochromic;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @OneToMany(mappedBy = "lens", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PrescriptionOrderDetail> prescriptionOrderDetails;

    public Lens(Product product, LensType lensType, BigDecimal indexValue, BigDecimal diameter,
                String availablePowerRange, Boolean isBlueLightBlock, Boolean isPhotochromic, String description) {
        this.product = product;
        this.lensType = lensType;
        this.indexValue = indexValue;
        this.diameter = diameter;
        this.availablePowerRange = availablePowerRange;
        this.isBlueLightBlock = isBlueLightBlock;
        this.isPhotochromic = isPhotochromic;
        this.description = description;
    }
}
