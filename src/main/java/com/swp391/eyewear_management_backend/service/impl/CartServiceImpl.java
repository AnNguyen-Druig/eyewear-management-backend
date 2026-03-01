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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.swp391.eyewear_management_backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
    private ContactLensRepository contactLensRepository;

    @Autowired
    private FrameRepository frameRepository;

    @Autowired
    private LensRepository lensRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CartItemPrescriptionRepository cartItemPrescriptionRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    /**
     * Lưu hoặc cập nhật sản phẩm trong giỏ hàng
     */
    @Override
    public CartItemResponse addOrUpdateCartItem(CartItemRequest request) {
        // Lấy user
        User user = getCurrentUser();

        // Lấy hoặc tạo mới giỏ hàng của user
        Cart cart = cartRepository.findByUserUserId(user.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Lấy Contact Lens, Frame, Lens từ repository
        ContactLens contactLens = null;
        if (request.getContactLensId() != null) {
            contactLens = contactLensRepository.findById(request.getContactLensId())
                    .orElseThrow(() -> new AppException(ErrorCode.CONTACT_LENS_NOT_FOUND));
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
        final ContactLens finalContactLens = contactLens;
        final Frame finalFrame = frame;
        final Lens finalLens = lens;

        // Kiểm tra xem sản phẩm này đã có trong giỏ chưa
//        CartItem existingItem = cart.getCartItems().stream()
//                .filter(item -> {
//                    boolean sameContactLens = (finalContactLens != null && finalContactLens.getContactLensID().equals(item.getContactLens() != null ? item.getContactLens().getContactLensID() : null));
//                    boolean sameFrame = (finalFrame != null && finalFrame.getFrameID().equals(item.getFrame() != null ? item.getFrame().getFrameID() : null));
//                    boolean sameLens = (finalLens != null && finalLens.getLensID().equals(item.getLens() != null ? item.getLens().getLensID() : null));
//                    return sameContactLens && sameFrame && sameLens;
//                })
//                .findFirst()
//                .orElse(null);


        Long targetContactLensId = (finalContactLens != null) ? finalContactLens.getContactLensID() : null;
        Long targetFrameId = (finalFrame != null) ? finalFrame.getFrameID() : null;
        Long targetLensId = (finalLens != null) ? finalLens.getLensID() : null;

        // 2. Tìm kiếm trong giỏ hàng
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> {
                    // Lấy ID của sản phẩm đang có trong giỏ
                    Long itemContactLensId = (item.getContactLens() != null) ? item.getContactLens().getContactLensID() : null;
                    Long itemFrameId = (item.getFrame() != null) ? item.getFrame().getFrameID() : null;
                    Long itemLensId = (item.getLens() != null) ? item.getLens().getLensID() : null;

                    // 3. So sánh bằng Objects.equals()
                    return Objects.equals(targetContactLensId, itemContactLensId) &&
                            Objects.equals(targetFrameId, itemFrameId) &&
                            Objects.equals(targetLensId, itemLensId);
                })
                .findFirst()
                .orElse(null);

        CartItem cartItem;
        CartItem savedItem;
        if (existingItem != null) {
            // Cập nhật quantity
            cartItem = existingItem;
            cartItem.setQuantity(request.getQuantity());
            savedItem = cartItemRepository.save(cartItem);
        } else {
            // Tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setContactLens(finalContactLens);
            cartItem.setFrame(finalFrame);
            cartItem.setLens(finalLens);
            cartItem.setQuantity(request.getQuantity());

            // Set giá từ database
            if (finalFrame != null) {
                cartItem.setFramePrice(finalFrame.getProduct().getPrice());
            }
            if (finalLens != null) {
                cartItem.setLensPrice(finalLens.getProduct().getPrice());
            }
            if (finalContactLens != null) {
                cartItem.setContactLensPrice(finalContactLens.getProduct().getPrice());
            }
            
            // Tính tổng giá
            BigDecimal totalPrice = BigDecimal.ZERO;
            if (finalFrame != null) {
                totalPrice = totalPrice.add(finalFrame.getProduct().getPrice());
            }
            if (finalLens != null) {
                totalPrice = totalPrice.add(finalLens.getProduct().getPrice());
            }
            if (finalContactLens != null) {
                totalPrice = totalPrice.add(finalContactLens.getProduct().getPrice());
            }
            cartItem.setPrice(totalPrice);

            savedItem = cartItemRepository.save(cartItem);

        }

        // Tạo hoặc cập nhật Prescription nếu có thông tin tròng kính
        if (hasPrescription(request)) {
            CartItemPrescription prescription = cartItemPrescriptionRepository
                    .findByCartItem(cartItem)
                    .orElse(null); // Nếu không tìm thấy thì gán bằng null ,yeah sure

            if (prescription == null) {
                // Nếu null (chưa có) thì mới tạo mới
                prescription = new CartItemPrescription();
                prescription.setCartItem(cartItem);
            }
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


        return cartItemMapper.toCartItemResponse(savedItem);
    }

    /**
     * Lấy tất cả sản phẩm trong giỏ hàng
     */
    @Override
    public List<CartItemResponse> getCartItems( ) {
        User user = getCurrentUser();
        Optional<Cart> cart = cartRepository.findByUserUserId(user.getUserId());

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
        User user = getCurrentUser();
        
        // Lấy cartItem
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        
        // Kiểm tra cartItem có thuộc cart của user không
        Cart cart = cartItem.getCart();
        if (cart != null && cart.getUser().getUserId().equals(user.getUserId())) {
            // Xóa nếu cartItem trùng user
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @Override
    public void clearCart() {
        User user = getCurrentUser();
        Optional<Cart> cart = cartRepository.findByUserUserId(user.getUserId());
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
