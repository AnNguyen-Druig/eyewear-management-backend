package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.response.extend.ContactLensResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.FrameResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.LensResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import com.swp391.eyewear_management_backend.entity.Product;
import com.swp391.eyewear_management_backend.entity.ProductImage;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "productID", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "brand.brandName", target = "brand")
    @Mapping(source = "productType.typeName", target = "product_Type")
    @Mapping(source = "images", target = "image_URL", qualifiedByName = "getAvatarUrl")
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

    default ProductDetailResponse toDetailResponse(Product product) {
        if (product.getFrame() != null) {
            return toFrameResponse(product);
        } else if (product.getLens() != null) {
            return toLensResponse(product);
        } else if (product.getContactLens() != null) {
            return toContactLensResponse(product);
        }
        // Trường hợp fallback nếu dữ liệu lỗi
        return null;
    }

    @Mapping(source = "productID", target = "id")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "SKU", target = "sku")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "brand.brandName", target = "brandName")
    @Mapping(target = "imageUrls", expression = "java(mapImages(product.getImages()))")
    @Mapping(source = "productType.typeName", target = "product_Type")
    ProductDetailResponse baseProductMapping(Product product);

    // 1. Map cho Frame
    @InheritConfiguration(name = "baseProductMapping")
    // Map các trường riêng của Frame
    @Mapping(source = "frame.color", target = "color")
    @Mapping(source = "frame.frameMaterialName", target = "material")
    @Mapping(source = "frame.frameShapeName", target = "frameShape")
    @Mapping(source = "frame.description", target = "description")
    @Mapping(source = "productType.typeName", target = "product_Type")
    @Mapping(target = "relatedFrames", ignore = true)
    @Mapping(target = "relatedLenses", ignore = true)
    FrameResponse toFrameResponse(Product product);

    // 2. Map cho Lens
    @InheritConfiguration(name = "baseProductMapping")
    // Map các trường riêng của Lens
    @Mapping(source = "lens.indexValue", target = "indexValue")
    @Mapping(source = "lens.description", target = "description")
    @Mapping(source = "productType.typeName", target = "product_Type")
    @Mapping(target = "relatedLenses", ignore = true)
    @Mapping(target = "relatedFrames", ignore = true)
    LensResponse toLensResponse(Product product);

    // 3. Map cho Contact Lens
    @InheritConfiguration(name = "baseProductMapping")
    // Map các trường riêng
    @Mapping(source = "contactLens.waterContent", target = "waterContent")
    @Mapping(source = "contactLens.diameter", target = "diameter")
    @Mapping(source = "productType.typeName", target = "product_Type")
    @Mapping(target = "relatedContactLenses", ignore = true)
    ContactLensResponse toContactLensResponse(Product product);

    // Helper map images (như cũ)
    default List<String> mapImages(List<ProductImage> images) {
        if (images == null) return null;
        return images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
    }
}