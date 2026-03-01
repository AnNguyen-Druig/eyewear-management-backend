package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "cart-items")
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartCartId(Long cartId);

    @Query("""
        select distinct ci
        from CartItem ci
        join fetch ci.cart c
        join fetch c.user u
        left join fetch ci.contactLens cl
        left join fetch cl.product clp
        left join fetch clp.brand
        left join fetch clp.productType
        left join fetch ci.frame f
        left join fetch f.product fp
        left join fetch fp.brand
        left join fetch fp.productType
        left join fetch ci.lens l
        left join fetch l.product lp
        left join fetch lp.brand
        left join fetch lp.productType
        where u.userId = :userId
          and ci.cartItemId in :ids
    """)
    List<CartItem> findByUserIdAndIdsFetchAll(Long userId, List<Long> ids);
}
