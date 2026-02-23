package com.swp391.eyewear_management_backend.entity;

import com.swp391.eyewear_management_backend.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cart_ID")
    private Long cartId; // Đổi thành Long

    // Giả sử class Entity của bảng User tên là User (hoặc UserEntity)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false, unique = true)
    private User user;

    @Column(name = "Created_At", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "Updated_At", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}