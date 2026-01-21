package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Brand")
@Data
@NoArgsConstructor
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

    public Brand(String brandName, String description, String logoUrl, Boolean status) {
        this.brandName = brandName;
        this.description = description;
        this.logoUrl = logoUrl;
        this.status = status;
    }
}
