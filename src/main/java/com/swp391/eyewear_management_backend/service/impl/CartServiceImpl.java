package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.CartItemRequest;
import com.swp391.eyewear_management_backend.dto.response.CartItemResponse;
import com.swp391.eyewear_management_backend.entity.*;
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

    /**
     * Lưu hoặc cập nhật sản phẩm trong giỏ hàng
     */
    @Override
    public CartItemResponse addOrUpdateCartItem(CartItemRequest request) {
        // Lấy user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

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
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
        }

        Frame frame = null;
        if (request.getFrameId() != null) {
            frame = frameRepository.findById(request.getFrameId())
                    .orElseThrow(() -> new RuntimeException("Frame not found with id: " + request.getFrameId()));
        }

        Lens lens = null;
        if (request.getLensId() != null) {
            lens = lensRepository.findById(request.getLensId())
                    .orElseThrow(() -> new RuntimeException("Lens not found with id: " + request.getLensId()));
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
                    return sameProduct || sameFrame || sameLens;
                })
                .findFirst()
                .orElse(null);

        CartItem cartItem;
        if (existingItem != null) {
            // Cập nhật quantity
            cartItem = existingItem;
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(finalProduct);
            cartItem.setFrame(finalFrame);
            cartItem.setLens(finalLens);
            cartItem.setQuantity(request.getQuantity());
        }

        // Set thông tin kính, tròng, cạnh
        cartItem.setRightEyeSph(request.getRightEyeSph());
        cartItem.setRightEyeCyl(request.getRightEyeCyl());
        cartItem.setRightEyeAxis(request.getRightEyeAxis());
        cartItem.setLeftEyeSph(request.getLeftEyeSph());
        cartItem.setLeftEyeCyl(request.getLeftEyeCyl());
        cartItem.setLeftEyeAxis(request.getLeftEyeAxis());

        CartItem savedItem = cartItemRepository.save(cartItem);

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
}
