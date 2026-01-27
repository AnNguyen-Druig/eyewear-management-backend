package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import com.swp391.eyewear_management_backend.entity.Product;
import com.swp391.eyewear_management_backend.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "images", target = "image", qualifiedByName = "getAvatarUrl")
    ProductResponse toProductResponse(Product product);

    @Named("getAvatarUrl")
    default String getAvatarUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return "default-placeholder.png";
        }

        return images.stream()
                .filter(ProductImage::getAvatar)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse("default-placeholder.png");
    }
}
