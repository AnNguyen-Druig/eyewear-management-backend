package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cart_Item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cart_Item_ID")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Cart_ID", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Frame_ID")
    private Frame frame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Lens_ID")
    private Lens lens;

    // ĐÃ SỬA: Thay precision và scale bằng columnDefinition = "DECIMAL(5,2)"
    @Column(name = "Right_Eye_Sph", columnDefinition = "DECIMAL(5,2)")
    private Double rightEyeSph;

    @Column(name = "Right_Eye_Cyl", columnDefinition = "DECIMAL(5,2)")
    private Double rightEyeCyl;

    @Column(name = "Right_Eye_Axis")
    private Integer rightEyeAxis;

    @Column(name = "Left_Eye_Sph", columnDefinition = "DECIMAL(5,2)")
    private Double leftEyeSph;

    @Column(name = "Left_Eye_Cyl", columnDefinition = "DECIMAL(5,2)")
    private Double leftEyeCyl;

    @Column(name = "Left_Eye_Axis")
    private Integer leftEyeAxis;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}