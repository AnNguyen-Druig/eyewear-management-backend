package com.swp391.eyewear_management_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Product_Image")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "product")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Image_ID")
    private Long imageID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "Product_ID", nullable = false)
    private Product product;

    @Column(name = "Image_URL", nullable = false, columnDefinition = "VARCHAR(MAX)")
    private String imageUrl;

    @Column(name = "Is_Avatar", nullable = false)
    private Boolean isAvatar = false;

    public Boolean getAvatar() {
        return isAvatar;
    }

    public ProductImage(Product product, String imageUrl, Boolean isAvatar) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.isAvatar = isAvatar;
    }
}
