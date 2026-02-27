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
    @Mapping(source = "cartItem.contactLens.contactLensID", target = "contactLensId")
    @Mapping(source = "cartItem.contactLens", target = "contactLensName", qualifiedByName = "getContactLensName")
    @Mapping(source = "cartItem.contactLens", target = "contactLensPrice", qualifiedByName = "getContactLensPrice")
    @Mapping(source = "cartItem.contactLens", target = "contactLensImg", qualifiedByName = "getContactLensImg")
    @Mapping(source = "cartItem.frame.frameID", target = "frameId")
    @Mapping(source = "cartItem.frame", target = "frameName", qualifiedByName = "getFrameName")
    @Mapping(source = "cartItem.framePrice", target = "framePrice")
    @Mapping(source = "cartItem.frame", target = "frameImg", qualifiedByName = "getFrameImg")
    @Mapping(source = "cartItem.lens.lensID", target = "lensId")
    @Mapping(source = "cartItem.lens", target = "lensName", qualifiedByName = "getLensName")
    @Mapping(source = "cartItem.lensPrice", target = "lensPrice")
    @Mapping(source = "cartItem.lens", target = "lensImg", qualifiedByName = "getLensImg")
    @Mapping(source = "cartItem.quantity", target = "quantity")
    @Mapping(source = "cartItem.price", target = "price")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Named("getContactLensName")
    default String getContactLensName(com.swp391.eyewear_management_backend.entity.ContactLens contactLens) {
        if (contactLens == null || contactLens.getProduct() == null) {
            return "Contact Lens";
        }
        return contactLens.getProduct().getProductName();
    }

    @Named("getContactLensPrice")
    default Double getContactLensPrice(com.swp391.eyewear_management_backend.entity.ContactLens contactLens) {
        if (contactLens == null || contactLens.getProduct() == null || contactLens.getProduct().getPrice() == null) {
            return 0.0;
        }
        return ((Number) contactLens.getProduct().getPrice()).doubleValue();
    }

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

    @Named("getFrameImg")
    default String getFrameImg(com.swp391.eyewear_management_backend.entity.Frame frame) {
        if (frame == null || frame.getProduct() == null || frame.getProduct().getImages() == null) {
            return null;
        }
        return frame.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getAvatar()))
                .map(com.swp391.eyewear_management_backend.entity.ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

    @Named("getLensName")
    default String getLensName(com.swp391.eyewear_management_backend.entity.Lens lens) {
        if (lens == null || lens.getProduct() == null) {
            return "Lens";
        }
        return lens.getProduct().getProductName();
    }

    @Named("getLensImg")
    default String getLensImg(com.swp391.eyewear_management_backend.entity.Lens lens) {
        if (lens == null || lens.getProduct() == null || lens.getProduct().getImages() == null) {
            return null;
        }
        return lens.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getAvatar()))
                .map(com.swp391.eyewear_management_backend.entity.ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

    @Named("getContactLensImg")
    default String getContactLensImg(com.swp391.eyewear_management_backend.entity.ContactLens contactLens) {
        if (contactLens == null || contactLens.getProduct() == null || contactLens.getProduct().getImages() == null) {
            return null;
        }
        return contactLens.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getAvatar()))
                .map(com.swp391.eyewear_management_backend.entity.ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }
}
