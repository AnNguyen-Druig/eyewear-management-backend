package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cart_Item_Prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemPrescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Prescription_ID")
    private Long prescriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Cart_Item_ID", nullable = false)
    private CartItem cartItem;

    // Right Eye Prescription
    @Column(name = "Right_Eye_Sph", columnDefinition = "DECIMAL(5,2)")
    private Double rightEyeSph;

    @Column(name = "Right_Eye_Cyl", columnDefinition = "DECIMAL(5,2)")
    private Double rightEyeCyl;

    @Column(name = "Right_Eye_Axis")
    private Integer rightEyeAxis;

    @Column(name = "Right_Eye_Add", columnDefinition = "DECIMAL(5,2)")
    private Double rightEyeAdd;

    // Left Eye Prescription
    @Column(name = "Left_Eye_Sph", columnDefinition = "DECIMAL(5,2)")
    private Double leftEyeSph;

    @Column(name = "Left_Eye_Cyl", columnDefinition = "DECIMAL(5,2)")
    private Double leftEyeCyl;

    @Column(name = "Left_Eye_Axis")
    private Integer leftEyeAxis;

    @Column(name = "Left_Eye_Add", columnDefinition = "DECIMAL(5,2)")
    private Double leftEyeAdd;

    // Pupillary Distance
    @Column(name = "PD", columnDefinition = "DECIMAL(4,1)")
    private Double pd;

    @Column(name = "PD_Right", columnDefinition = "DECIMAL(4,1)")
    private Double pdRight;

    @Column(name = "PD_Left", columnDefinition = "DECIMAL(4,1)")
    private Double pdLeft;
}
