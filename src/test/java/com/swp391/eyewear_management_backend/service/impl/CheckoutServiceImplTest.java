package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.CheckoutPreviewRequest;
import com.swp391.eyewear_management_backend.dto.request.ShippingAddressRequest;
import com.swp391.eyewear_management_backend.dto.response.PromotionCandidateResponse;
import com.swp391.eyewear_management_backend.entity.CartItem;
import com.swp391.eyewear_management_backend.entity.ContactLens;
import com.swp391.eyewear_management_backend.entity.Product;
import com.swp391.eyewear_management_backend.entity.User;
import com.swp391.eyewear_management_backend.repository.CartItemPrescriptionRepo;
import com.swp391.eyewear_management_backend.repository.CartItemRepo;
import com.swp391.eyewear_management_backend.repository.UserRepo;
import com.swp391.eyewear_management_backend.service.GhnShippingService;
import com.swp391.eyewear_management_backend.service.PromotionCalculatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {
    @Mock
    CartItemRepo cartItemRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    PromotionCalculatorService promotionCalculatorService;

    @Mock
    GhnShippingService ghnShippingService;

    @Mock
    CartItemPrescriptionRepo cartItemPrescriptionRepo;

    @InjectMocks
    CheckoutServiceImpl checkoutService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void preview_whenAvailableEnough_shouldClassifyDirectOrder() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("u1", "x", List.of(() -> "ROLE_USER"))
        );

        User user = new User();
        user.setUserId(1L);
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));

        Product p = new Product();
        p.setProductID(1L);
        p.setProductName("P1");
        p.setAllowPreorder(true);
        p.setAvailableQuantity(47);

        ContactLens cl = new ContactLens();
        cl.setContactLensID(10L);
        cl.setProduct(p);

        CartItem ci = new CartItem();
        ci.setCartItemId(100L);
        ci.setContactLens(cl);
        ci.setQuantity(1);
        ci.setPrice(new BigDecimal("1000000"));

        when(cartItemRepo.findByUserIdAndIdsFetchAll(1L, List.of(100L))).thenReturn(List.of(ci));
        when(cartItemPrescriptionRepo.findByCartItem_CartItemIdIn(List.of(100L))).thenReturn(List.of());
        when(promotionCalculatorService.evaluate(List.of(ci), new BigDecimal("1000000"), null))
                .thenReturn(new PromotionCalculatorService.PromotionResult(
                        BigDecimal.ZERO, null, List.of(), (PromotionCandidateResponse) null, Map.of()
                ));
        when(ghnShippingService.calculate(any(), any()))
                .thenReturn(new GhnShippingService.ShippingResult(BigDecimal.ZERO, LocalDateTime.now()));

        CheckoutPreviewRequest req = new CheckoutPreviewRequest();
        req.setCartItemIds(List.of(100L));
        ShippingAddressRequest addr = new ShippingAddressRequest();
        addr.setDistrictCode(1);
        addr.setWardCode("1");
        req.setAddress(addr);
        CheckoutPreviewRequest request = req;

        var resp = checkoutService.preview(request);

        assertThat(resp.getItems()).hasSize(1);
        assertThat(resp.getItems().get(0).getItemType()).isEqualTo("DIRECT");
        assertThat(resp.getOrderType()).isEqualTo("DIRECT_ORDER");
    }

    @Test
    void preview_whenAvailableInsufficientAndAllowPreorder_shouldClassifyPreOrder() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("u1", "x", List.of(() -> "ROLE_USER"))
        );

        User user = new User();
        user.setUserId(1L);
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));

        Product p = new Product();
        p.setProductID(1L);
        p.setProductName("P1");
        p.setAllowPreorder(true);
        p.setAvailableQuantity(0);

        ContactLens cl = new ContactLens();
        cl.setContactLensID(10L);
        cl.setProduct(p);

        CartItem ci = new CartItem();
        ci.setCartItemId(100L);
        ci.setContactLens(cl);
        ci.setQuantity(1);
        ci.setPrice(new BigDecimal("1000000"));

        when(cartItemRepo.findByUserIdAndIdsFetchAll(1L, List.of(100L))).thenReturn(List.of(ci));
        when(cartItemPrescriptionRepo.findByCartItem_CartItemIdIn(List.of(100L))).thenReturn(List.of());
        when(promotionCalculatorService.evaluate(List.of(ci), new BigDecimal("1000000"), null))
                .thenReturn(new PromotionCalculatorService.PromotionResult(
                        BigDecimal.ZERO, null, List.of(), (PromotionCandidateResponse) null, Map.of()
                ));
        when(ghnShippingService.calculate(any(), any()))
                .thenReturn(new GhnShippingService.ShippingResult(BigDecimal.ZERO, LocalDateTime.now()));

        CheckoutPreviewRequest req = new CheckoutPreviewRequest();
        req.setCartItemIds(List.of(100L));
        ShippingAddressRequest addr = new ShippingAddressRequest();
        addr.setDistrictCode(1);
        addr.setWardCode("1");
        req.setAddress(addr);

        var resp = checkoutService.preview(req);

        assertThat(resp.getItems()).hasSize(1);
        assertThat(resp.getItems().get(0).getItemType()).isEqualTo("PRE_ORDER");
        assertThat(resp.getOrderType()).isEqualTo("PRE_ORDER");
    }
}
