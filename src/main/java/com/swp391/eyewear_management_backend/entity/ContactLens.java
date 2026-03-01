package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "Contact_Lens")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "product")
public class ContactLens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Contact_Lens_ID")
    private Long contactLensID;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false, unique = true)
    private Product product;

    @Column(name = "Usage_Type", columnDefinition = "NVARCHAR(50)")
    private String usageType;

    @Column(name = "Base_Curve", precision = 5, scale = 2)
    private BigDecimal baseCurve;

    @Column(name = "Diameter", precision = 5, scale = 2)
    private BigDecimal diameter;

    @Column(name = "Water_Content", precision = 5, scale = 2)
    private BigDecimal waterContent;

    @Column(name = "Available_Power_Range", columnDefinition = "NVARCHAR(200)")
    private String availablePowerRange;

    @Column(name = "Quantity_Per_Box")
    private Integer quantityPerBox;

    @Column(name = "Lens_Material", columnDefinition = "NVARCHAR(50)")
    private String lensMaterial;

    @Column(name = "Replacement_Schedule", columnDefinition = "NVARCHAR(50)")
    private String replacementSchedule;

    @Column(name = "Color", columnDefinition = "NVARCHAR(50)")
    private String color;

    public ContactLens(Product product, String usageType, BigDecimal baseCurve, BigDecimal diameter,
                       BigDecimal waterContent, String availablePowerRange, Integer quantityPerBox,
                       String lensMaterial, String replacementSchedule, String color) {
        this.product = product;
        this.usageType = usageType;
        this.baseCurve = baseCurve;
        this.diameter = diameter;
        this.waterContent = waterContent;
        this.availablePowerRange = availablePowerRange;
        this.quantityPerBox = quantityPerBox;
        this.lensMaterial = lensMaterial;
        this.replacementSchedule = replacementSchedule;
        this.color = color;
    }
}
