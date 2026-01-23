package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Prescription_Order")
@Data
@NoArgsConstructor
public class PrescriptionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Prescription_Order_ID")
    private Long prescriptionOrderID;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Order_ID", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name = "Prescription_Date", nullable = false)
    private LocalDateTime prescriptionDate;

    @Column(name = "Note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "Complete_Date")
    private LocalDate completeDate;

    @OneToMany(mappedBy = "prescriptionOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PrescriptionOrderDetail> prescriptionOrderDetails;

    public PrescriptionOrder(Order order, User user, LocalDateTime prescriptionDate, String note, LocalDate completeDate) {
        this.order = order;
        this.user = user;
        this.prescriptionDate = prescriptionDate;
        this.note = note;
        this.completeDate = completeDate;
    }
}
