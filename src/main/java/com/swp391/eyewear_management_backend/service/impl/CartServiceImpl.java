package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.CartItemRequest;
import com.swp391.eyewear_management_backend.dto.response.CartItemResponse;
import com.swp391.eyewear_management_backend.entity.*;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.mapper.CartItemMapper;
import com.swp391.eyewear_management_backend.repository.*;
import com.swp391.eyewear_management_backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private FrameRepository frameRepository;

    @Autowired
    private LensRepository lensRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CartItemPrescriptionRepository cartItemPrescriptionRepository;

    /**
     * Lưu hoặc cập nhật sản phẩm trong giỏ hàng
     */
    @Override
    public CartItemResponse addOrUpdateCartItem(CartItemRequest request) {
        // Lấy user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy hoặc tạo mới giỏ hàng của user
        Cart cart = cartRepository.findByUserUserId(request.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Lấy Product, Frame, Lens từ repository
        Product product = null;
        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        }

        Frame frame = null;
        if (request.getFrameId() != null) {
            frame = frameRepository.findById(request.getFrameId())
                    .orElseThrow(() -> new AppException(ErrorCode.FRAME_NOT_FOUND));
        }

        Lens lens = null;
        if (request.getLensId() != null) {
            lens = lensRepository.findById(request.getLensId())
                    .orElseThrow(() -> new AppException(ErrorCode.LENS_NOT_FOUND));
        }

        // Khai báo final để dùng trong lambda
        final Product finalProduct = product;
        final Frame finalFrame = frame;
        final Lens finalLens = lens;

        // Kiểm tra xem sản phẩm này đã có trong giỏ chưa
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> {
                    boolean sameProduct = (finalProduct != null && finalProduct.getProductID().equals(item.getProduct() != null ? item.getProduct().getProductID() : null));
                    boolean sameFrame = (finalFrame != null && finalFrame.getFrameID().equals(item.getFrame() != null ? item.getFrame().getFrameID() : null));
                    boolean sameLens = (finalLens != null && finalLens.getLensID().equals(item.getLens() != null ? item.getLens().getLensID() : null));
                    return sameProduct && sameFrame && sameLens;
                })
                .findFirst()
                .orElse(null);

        CartItem cartItem;
        CartItem savedItem;
        if (existingItem != null) {
            // Cập nhật quantity
            cartItem = existingItem;
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            savedItem = cartItemRepository.save(cartItem);
        } else {
            // Tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(finalProduct);
            cartItem.setFrame(finalFrame);
            cartItem.setLens(finalLens);
            cartItem.setQuantity(request.getQuantity());

            // Set giá
            cartItem.setFramePrice(request.getFramePrice());
            cartItem.setLensPrice(request.getLensPrice());
            cartItem.setPrice(request.getPrice()); // kiểm tra lại

            savedItem = cartItemRepository.save(cartItem);

            // Tạo hoặc cập nhật Prescription nếu có thông tin tròng kính
            if (hasPrescription(request)) {
                CartItemPrescription prescription = new CartItemPrescription();
                prescription.setCartItem(savedItem);
                prescription.setRightEyeSph(request.getRightEyeSph());
                prescription.setRightEyeCyl(request.getRightEyeCyl());
                prescription.setRightEyeAxis(request.getRightEyeAxis());
                prescription.setRightEyeAdd(request.getRightEyeAdd());
                prescription.setLeftEyeSph(request.getLeftEyeSph());
                prescription.setLeftEyeCyl(request.getLeftEyeCyl());
                prescription.setLeftEyeAxis(request.getLeftEyeAxis());
                prescription.setLeftEyeAdd(request.getLeftEyeAdd());
                prescription.setPd(request.getPd());
                prescription.setPdRight(request.getPdRight());
                prescription.setPdLeft(request.getPdLeft());
                cartItemPrescriptionRepository.save(prescription);
            }
        }



        return cartItemMapper.toCartItemResponse(savedItem);
    }

    /**
     * Lấy tất cả sản phẩm trong giỏ hàng
     */
    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        Optional<Cart> cart = cartRepository.findByUserUserId(userId);

        if (cart.isEmpty()) {
            return List.of();
        }

        return cart.get().getCartItems().stream()
                .map(cartItemMapper::toCartItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     */
    @Override
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Override
    public void clearCart(Long userId) {
        Optional<Cart> cart = cartRepository.findByUserUserId(userId);
        if (cart.isPresent()) {
            cart.get().getCartItems().clear();
            cartRepository.save(cart.get());
        }

    }

    /**
     * Kiểm tra có prescription fields nào không
     */
    private boolean hasPrescription (CartItemRequest request){
        return request.getRightEyeSph() != null ||
                request.getRightEyeCyl() != null ||
                request.getRightEyeAxis() != null ||
                request.getRightEyeAdd() != null ||
                request.getLeftEyeSph() != null ||
                request.getLeftEyeCyl() != null ||
                request.getLeftEyeAxis() != null ||
                request.getLeftEyeAdd() != null ||
                request.getPd() != null ||
                request.getPdRight() != null ||
                request.getPdLeft() != null;
    }
}
