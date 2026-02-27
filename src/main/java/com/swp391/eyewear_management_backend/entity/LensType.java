package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "Lens_Type")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "lenses")
public class LensType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Lens_Type_ID")
    private Long lensTypeID;

    @Column(name = "Type_Name", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String typeName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @OneToMany(mappedBy = "lensType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Lens> lenses;

    public LensType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
    }
}
