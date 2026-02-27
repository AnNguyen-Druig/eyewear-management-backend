package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.dto.request.CartItemRequest;
import com.swp391.eyewear_management_backend.dto.response.CartItemResponse;
import com.swp391.eyewear_management_backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * API để lưu loại kính, tròng và kính đi liền với tròng vào giỏ hàng
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @Valid @RequestBody CartItemRequest request) {
        CartItemResponse response = cartService.addOrUpdateCartItem(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API để lấy tất cả sản phẩm trong giỏ hàng
     * GET /api/cart/{userId}
     */
    @GetMapping("/getdetail/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long userId) {
        List<CartItemResponse> items = cartService.getCartItems(userId);
        return ResponseEntity.ok(items);
    }

    /**
     * API để xóa một sản phẩm khỏi giỏ hàng
     * DELETE /api/cart/item/{cartItemId}
     */
    @DeleteMapping("/deleteall/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * API để xóa toàn bộ giỏ hàng
     * DELETE /api/cart/{userId}
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
