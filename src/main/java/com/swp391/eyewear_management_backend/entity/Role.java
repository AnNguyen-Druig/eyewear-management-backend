package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Role")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Role_ID")
    private Long roleID;

    @Column(name = "Type_Name", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String typeName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<User> users;

    public Role(String typeName) {
        this.typeName = typeName;
    }
}
