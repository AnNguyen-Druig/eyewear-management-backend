package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.request.CartItemRequest;
import com.swp391.eyewear_management_backend.dto.response.CartItemResponse;

import java.util.List;

public interface CartService {

    /**
     * Lưu hoặc cập nhật sản phẩm trong giỏ hàng
     * @param request Thông tin sản phẩm cần lưu
     * @return CartItemResponse
     */
    CartItemResponse addOrUpdateCartItem(CartItemRequest request);





    /**
     * Lấy tất cả sản phẩm trong giỏ hàng của user
     * ID của user
     * @return Danh sách CartItemResponse
     */
    List<CartItemResponse> getCartItems();

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     * @param cartItemId ID của cart item cần xóa
     */
    void deleteCartItem(Long cartItemId);

    /**
     * Cập nhật các thuộc tính của sản phẩm trong giỏ hàng
     * @param request Thông tin cập nhật (cartItemId, frameId, lensId, contactLensId, quantity, prescription)
     * @return CartItemResponse
     */
    CartItemResponse updateCartItem(CartItemRequest request);

    /**
     * Xóa toàn bộ giỏ hàng
     *
     */
    void clearCart();
}
