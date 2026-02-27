package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "Prescription_Order_Detail")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"prescriptionOrder", "frame", "lens"})
public class PrescriptionOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Prescription_Order_Detail_ID")
    private Long prescriptionOrderDetailID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Prescription_Order_ID", nullable = false)
    private PrescriptionOrder prescriptionOrder;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Frame_ID")
    private Frame frame;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Lens_ID")
    private Lens lens;

    @Column(name = "Right_Eye_Sph", precision = 5, scale = 2)
    private BigDecimal rightEyeSph;

    @Column(name = "Right_Eye_Cyl", precision = 5, scale = 2)
    private BigDecimal rightEyeCyl;

    @Column(name = "Right_Eye_Axis")
    private Integer rightEyeAxis;

    @Column(name = "Left_Eye_Sph", precision = 5, scale = 2)
    private BigDecimal leftEyeSph;

    @Column(name = "Left_Eye_Cyl", precision = 5, scale = 2)
    private BigDecimal leftEyeCyl;

    @Column(name = "Left_Eye_Axis")
    private Integer leftEyeAxis;

    @Column(name = "Sub_Total", nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotal;

    public PrescriptionOrderDetail(PrescriptionOrder prescriptionOrder, Frame frame, Lens lens,
                                   BigDecimal rightEyeSph, BigDecimal rightEyeCyl, Integer rightEyeAxis,
                                   BigDecimal leftEyeSph, BigDecimal leftEyeCyl, Integer leftEyeAxis,
                                   BigDecimal subTotal) {
        this.prescriptionOrder = prescriptionOrder;
        this.frame = frame;
        this.lens = lens;
        this.rightEyeSph = rightEyeSph;
        this.rightEyeCyl = rightEyeCyl;
        this.rightEyeAxis = rightEyeAxis;
        this.leftEyeSph = leftEyeSph;
        this.leftEyeCyl = leftEyeCyl;
        this.leftEyeAxis = leftEyeAxis;
        this.subTotal = subTotal;
    }
}
