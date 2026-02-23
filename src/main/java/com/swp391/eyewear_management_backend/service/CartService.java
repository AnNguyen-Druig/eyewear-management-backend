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
     * @param userId ID của user
     * @return Danh sách CartItemResponse
     */
    List<CartItemResponse> getCartItems(Long userId);

    /**
     * Xóa một sản phẩm khỏi giỏ hàng
     * @param cartItemId ID của cart item cần xóa
     */
    void deleteCartItem(Long cartItemId);

    /**
     * Xóa toàn bộ giỏ hàng
     * @param userId ID của user
     */
    void clearCart(Long userId);
}
