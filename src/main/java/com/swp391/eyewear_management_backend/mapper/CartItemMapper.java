package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.response.CartItemResponse;
import com.swp391.eyewear_management_backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "cartItem.cartItemId", target = "cartItemId")
    @Mapping(source = "cartItem.cart.cartId", target = "cartId")
    @Mapping(source = "cartItem.product.productID", target = "productId")
    @Mapping(source = "cartItem.product.productName", target = "productName")
    @Mapping(source = "cartItem.product.price", target = "productPrice", qualifiedByName = "convertPrice")
    @Mapping(source = "cartItem.frame.frameID", target = "frameId")
    @Mapping(source = "cartItem.frame", target = "frameName", qualifiedByName = "getFrameName")
    @Mapping(source = "cartItem.frame", target = "framePrice", qualifiedByName = "getFramePrice")
    @Mapping(source = "cartItem.lens.lensID", target = "lensId")
    @Mapping(source = "cartItem.lens", target = "lensName", qualifiedByName = "getLensName")
    @Mapping(source = "cartItem.lens", target = "lensPrice", qualifiedByName = "getLensPrice")
    @Mapping(source = "cartItem", target = "totalPrice", qualifiedByName = "calculateTotalPrice")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Named("convertPrice")
    default Double convertPrice(Object price) {
        if (price == null) {
            return 0.0;
        }
        return ((Number) price).doubleValue();
    }

    @Named("getFrameName")
    default String getFrameName(com.swp391.eyewear_management_backend.entity.Frame frame) {
        if (frame == null || frame.getProduct() == null) {
            return "Frame";
        }
        return frame.getProduct().getProductName();
    }

    @Named("getFramePrice")
    default Double getFramePrice(com.swp391.eyewear_management_backend.entity.Frame frame) {
        if (frame == null || frame.getProduct() == null) {
            return 0.0;
        }
        Object price = frame.getProduct().getPrice();
        return price != null ? ((Number) price).doubleValue() : 0.0;
    }

    @Named("getLensName")
    default String getLensName(com.swp391.eyewear_management_backend.entity.Lens lens) {
        if (lens == null || lens.getProduct() == null) {
            return "Lens";
        }
        return lens.getProduct().getProductName();
    }

    @Named("getLensPrice")
    default Double getLensPrice(com.swp391.eyewear_management_backend.entity.Lens lens) {
        if (lens == null || lens.getProduct() == null) {
            return 0.0;
        }
        Object price = lens.getProduct().getPrice();
        return price != null ? ((Number) price).doubleValue() : 0.0;
    }

    @Named("calculateTotalPrice")
    default Double calculateTotalPrice(CartItem cartItem) {
        double totalPrice = 0;
        
        if (cartItem.getProduct() != null && cartItem.getProduct().getPrice() != null) {
            totalPrice += ((Number) cartItem.getProduct().getPrice()).doubleValue() * cartItem.getQuantity();
        }
        
        if (cartItem.getFrame() != null && cartItem.getFrame().getProduct() != null 
                && cartItem.getFrame().getProduct().getPrice() != null) {
            totalPrice += ((Number) cartItem.getFrame().getProduct().getPrice()).doubleValue() * cartItem.getQuantity();
        }
        
        if (cartItem.getLens() != null && cartItem.getLens().getProduct() != null 
                && cartItem.getLens().getProduct().getPrice() != null) {
            totalPrice += ((Number) cartItem.getLens().getProduct().getPrice()).doubleValue() * cartItem.getQuantity();
        }
        
        return totalPrice;
    }
}
