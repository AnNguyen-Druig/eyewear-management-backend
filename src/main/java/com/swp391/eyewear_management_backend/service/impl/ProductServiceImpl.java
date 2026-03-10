package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.ProductCreateRequest;
import com.swp391.eyewear_management_backend.dto.request.ProductUpdateRequest;
import com.swp391.eyewear_management_backend.dto.response.ProductDetailResponse;
import com.swp391.eyewear_management_backend.dto.response.ProductResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.ContactLensResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.FrameResponse;
import com.swp391.eyewear_management_backend.dto.response.extend.LensResponse;
import com.swp391.eyewear_management_backend.entity.Brand;
import com.swp391.eyewear_management_backend.entity.Product;
import com.swp391.eyewear_management_backend.entity.ProductImage;
import com.swp391.eyewear_management_backend.entity.ProductType;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.mapper.ProductMapper;
import com.swp391.eyewear_management_backend.repository.BrandRepo;
import com.swp391.eyewear_management_backend.repository.ProductImageRepo;
import com.swp391.eyewear_management_backend.repository.ProductRepo;
import com.swp391.eyewear_management_backend.repository.ProductTypeRepo;
import com.swp391.eyewear_management_backend.service.ImageUploadService;
import com.swp391.eyewear_management_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private BrandRepo brandRepository;

    @Autowired
    private ProductTypeRepo productTypeRepository;

    @Autowired
    private ProductImageRepo productImageRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductResponse> searchProducts(String name, Double minPrice, Double maxPrice, String brand) {
        List<Product> products = productRepository.searchProducts(name, minPrice, maxPrice, brand);
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDetailResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // MapStruct tự động chọn trả về FrameResponse hay LensResponse
        ProductDetailResponse response = productMapper.toDetailResponse(product);
        
        // Populate related products based on type
        if (response instanceof FrameResponse) {
            FrameResponse frameResponse = (FrameResponse) response;
            frameResponse.setFrameId(product.getFrame().getFrameID());
            populateRelatedProducts(frameResponse, product.getProductID());
        } else if (response instanceof LensResponse) {
            LensResponse lensResponse = (LensResponse) response;
            lensResponse.setLensId(product.getLens().getLensID());
            populateRelatedProducts(lensResponse, product.getProductID());
        } else if (response instanceof ContactLensResponse) {
            ContactLensResponse contactLensResponse = (ContactLensResponse) response;
            contactLensResponse.setContactLensId(product.getContactLens().getContactLensID());
            populateRelatedProducts(contactLensResponse, product.getProductID());
        }
        
        return response;
    }
    
    private void populateRelatedProducts(FrameResponse response, Long currentProductId) {
        // Lấy 4 gọng khác
        List<Product> relatedFrames = productRepository.findByProductTypeNameExcludingId("Gọng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedFrames(relatedFrames.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
        
        // Lấy 4 tròng kính
        List<Product> relatedLenses = productRepository.findByProductTypeNameExcludingId("Tròng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedLenses(relatedLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }
    
    private void populateRelatedProducts(LensResponse response, Long currentProductId) {
        // Lấy 4 tròng kính khác
        List<Product> relatedLenses = productRepository.findByProductTypeNameExcludingId("Tròng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedLenses(relatedLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
        
        // Lấy 4 gọng kính
        List<Product> relatedFrames = productRepository.findByProductTypeNameExcludingId("Gọng kính", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedFrames(relatedFrames.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }
    
    private void populateRelatedProducts(ContactLensResponse response, Long currentProductId) {
        // Lấy 4 kính áp tròng khác
        List<Product> relatedContactLenses = productRepository.findByProductTypeNameExcludingId("Kính áp tròng", currentProductId)
                .stream().limit(4).collect(Collectors.toList());
        response.setRelatedContactLenses(relatedContactLenses.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_SALES STAFF','ROLE_ADMIN','ROLE_MANAGER')")
    public ProductResponse createProduct(ProductCreateRequest request, List<MultipartFile> imageFiles) throws IOException {
        // 1. Kiểm tra SKU bắt buộc phải nhập
        if (request.getSku() == null || request.getSku().trim().isEmpty()) {
            throw new AppException(ErrorCode.SKU_REQUIRED);
        }
        
        // 2. Kiểm tra SKU đã tồn tại chưa
        String skuInput = request.getSku().trim();
        if (productRepository.existsBySKU(skuInput)) {
            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS);
        }
        
        // 3. Tạo product mới
        Product product = new Product();
        
        // 4. Set thông tin cơ bản
        product.setProductName(request.getName());
        product.setSKU(request.getSku());
        product.setPrice(BigDecimal.valueOf(request.getPrice()));
        product.setCostPrice(request.getCostPrice() != null ? 
                BigDecimal.valueOf(request.getCostPrice()) : BigDecimal.valueOf(request.getPrice()));
        product.setDescription(request.getDescription());
        product.setAllowPreorder(request.getAllowPreorder() != null ? request.getAllowPreorder() : false);
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        // 5. Xử lý Brand (Thương hiệu) - Tìm hoặc tạo mới
        if (request.getBrandName() == null || request.getBrandName().trim().isEmpty()) {
            throw new RuntimeException("Tên thương hiệu không được để trống");
        }
        String brandNameInput = request.getBrandName().trim();
        Brand brand = brandRepository.findByBrandName(brandNameInput)
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setBrandName(brandNameInput);
                    newBrand.setStatus(true);
                    return brandRepository.save(newBrand);
                });
        product.setBrand(brand);
        
        // 6. Xử lý Product Type (Loại sản phẩm) - Tìm hoặc tạo mới
        if (request.getTypeName() == null || request.getTypeName().trim().isEmpty()) {
            throw new RuntimeException("Loại sản phẩm không được để trống");
        }
        String typeNameInput = request.getTypeName().trim();
        ProductType type = productTypeRepository.findByTypeName(typeNameInput)
                .orElseGet(() -> {
                    ProductType newType = new ProductType();
                    newType.setTypeName(typeNameInput);
                    return productTypeRepository.save(newType);
                });
        product.setProductType(type);
        
        // 7. Lưu product
        Product savedProduct = productRepository.save(product);
        
        // 8. Xử lý upload nhiều ảnh nếu có
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                if (file != null && !file.isEmpty()) {
                    String imageUrl = imageUploadService.uploadImage(file);
                    // Ảnh đầu tiên sẽ là ảnh đại diện (isAvatar = true)
                    boolean isAvatar = (i == 0);
                    ProductImage productImage = new ProductImage(savedProduct, imageUrl, isAvatar);
                    productImageRepository.save(productImage);
                }
            }
        }
        
        // 9. Trả về response
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_SALES STAFF','ROLE_ADMIN','ROLE_MANAGER')")
    public ProductResponse updateProduct(ProductUpdateRequest request) {
        // 1. Tìm sản phẩm hiện tại
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + request.getId()));

        // 2. Cập nhật thông tin cơ bản
        if (request.getName() != null) product.setProductName(request.getName());
        if (request.getSku() != null) product.setSKU(request.getSku());
        if (request.getPrice() != null) product.setPrice(BigDecimal.valueOf(request.getPrice()));
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());

        // 3. Xử lý Brand (Thương hiệu)
        if (request.getBrandName() != null && !request.getBrandName().trim().isEmpty()) {
            String brandNameInput = request.getBrandName().trim();
            Brand brand = brandRepository.findByBrandName(brandNameInput)
                    .orElseGet(() -> {
                        // Nếu chưa có thì tạo mới
                        Brand newBrand = new Brand();
                        newBrand.setBrandName(brandNameInput);
                        newBrand.setStatus(true); // Set status mặc định là 1 (Active) dựa theo hình ảnh DB của bạn
                        return brandRepository.save(newBrand);
                    });
            product.setBrand(brand);
        }

        // 4. Xử lý Product Type (Loại sản phẩm)
        if (request.getTypeName() != null && !request.getTypeName().trim().isEmpty()) {
            String typeNameInput = request.getTypeName().trim();
            ProductType type = productTypeRepository.findByTypeName(typeNameInput)
                    .orElseGet(() -> {
                        // Nếu chưa có thì tạo mới
                        ProductType newType = new ProductType();
                        newType.setTypeName(typeNameInput);
                        // Có thể thêm description mặc định nếu cần
                        return productTypeRepository.save(newType);
                    });
            product.setProductType(type);
        }

        // 5. Lưu sản phẩm và trả về
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_SALES STAFF','ROLE_ADMIN','ROLE_MANAGER')")
    public void deleteProduct(Long id) {
        // 1. Tìm sản phẩm
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // 2. Soft delete: Đặt isActive thành false thay vì xóa thật
        product.setIsActive(false);
        productRepository.save(product);
    }

}
