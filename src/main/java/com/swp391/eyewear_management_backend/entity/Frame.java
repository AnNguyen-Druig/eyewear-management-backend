package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Frame")
@Data
@NoArgsConstructor
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Frame_ID")
    private Long frameID;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false, unique = true)
    private Product product;

    @Column(name = "Color", columnDefinition = "NVARCHAR(50)")
    private String color;

    @Column(name = "Temple_Length", precision = 5, scale = 2)
    private BigDecimal templeLength;

    @Column(name = "Lens_Width", precision = 5, scale = 2)
    private BigDecimal lensWidth;

    @Column(name = "Bridge_Width", precision = 5, scale = 2)
    private BigDecimal bridgeWidth;

    @Column(name = "Frame_Shape_Name", columnDefinition = "NVARCHAR(255)")
    private String frameShapeName;

    @Column(name = "Frame_Material_Name", columnDefinition = "NVARCHAR(255)")
    private String frameMaterialName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    public Frame(Product product, String color, BigDecimal templeLength, BigDecimal lensWidth,
                 BigDecimal bridgeWidth, String frameShapeName, String frameMaterialName, String description) {
        this.product = product;
        this.color = color;
        this.templeLength = templeLength;
        this.lensWidth = lensWidth;
        this.bridgeWidth = bridgeWidth;
        this.frameShapeName = frameShapeName;
        this.frameMaterialName = frameMaterialName;
        this.description = description;
    }
}
