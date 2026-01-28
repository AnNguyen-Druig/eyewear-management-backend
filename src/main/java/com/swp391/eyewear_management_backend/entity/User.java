package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "[User]")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Long userId;

    @Column(name = "Username", unique = true, nullable = false, columnDefinition = "NVARCHAR(50)")
    private String username;

    @Column(name = "Password", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String password;

    @Column(name = "Email", unique = true, nullable = false, columnDefinition = "NVARCHAR(100)")
    private String email;

    @Column(name = "Phone", nullable = false, columnDefinition = "VARCHAR(15)")
    private String phone;

    @ManyToOne
    @JoinColumn(name = "Role_ID")
    private Role role;

    @Column(name = "Status")
    private Boolean status;

    @Column(name = "Name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "Address", columnDefinition = "NVARCHAR(255)")
    private String address;

    @Column(name = "Date_of_Birth")
    private LocalDate dateOfBirth;

    @Column(name = "ID_Number", columnDefinition = "VARCHAR(20)")
    private String idNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "changedBy", fetch = FetchType.LAZY)
    private List<OrderProcessing> orderProcessings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReturnExchange> returnExchanges;

    @OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
    private List<ReturnExchange> approvedReturnExchanges;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PrescriptionOrder> prescriptionOrders;

    public User(String username, String password, String email, String phone, Role role, Boolean status, String name, String address, LocalDate dateOfBirth, String idNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
    }
}
