package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.CheckoutPreviewRequest;
import com.swp391.eyewear_management_backend.dto.request.CreateOrderRequest;
import com.swp391.eyewear_management_backend.dto.request.ShippingAddressRequest;
import com.swp391.eyewear_management_backend.dto.response.CheckoutPreviewResponse;
import com.swp391.eyewear_management_backend.dto.response.CreateOrderResponse;
import com.swp391.eyewear_management_backend.entity.*;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.repository.*;
import com.swp391.eyewear_management_backend.service.CheckoutService;
import com.swp391.eyewear_management_backend.service.OrderService;
import com.swp391.eyewear_management_backend.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepo userRepo;
    private final CartItemRepo cartItemRepo;

    private final OrderRepo orderRepo;
    private final ShippingInfoRepo shippingInfoRepo;
    private final PaymentRepo paymentRepo;
    private final InvoiceRepo invoiceRepo;

    private final PromotionRepo promotionRepo;
    private final CartItemPrescriptionRepo cartItemPrescriptionRepo;

    private final CheckoutService checkoutService; // reuse preview calculation
    private final PaymentGatewayService paymentGatewayService; // stub for now

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        // 1) current user (fix NPE getName)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            // đổi ErrorCode theo project bạn nếu khác
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2) chuẩn hóa ids
        var ids = request.getCartItemIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (ids.isEmpty()) throw new AppException(ErrorCode.INVALID_REQUEST);

        // 3) build address: nếu request không gửi => dùng default của user
        ShippingAddressRequest address = resolveAddress(request.getAddress(), user);

        // 4) chạy preview nội bộ để lấy tất cả số tiền chuẩn
        CheckoutPreviewRequest previewReq = new CheckoutPreviewRequest();
        previewReq.setCartItemIds(ids);
        previewReq.setPromotionId(request.getPromotionId());
        previewReq.setPaymentMethod(request.getPaymentMethod());
        previewReq.setAddress(address);

        CheckoutPreviewResponse preview = checkoutService.preview(previewReq);

        // 5) validate payment strategy theo preview.depositRequired
        PaymentPlan plan = buildPaymentPlan(preview, request);

        // 6) INSERT Order
        // (giữ theo entity của bạn: Order có orderID, promotion là entity)
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(generateOrderCode());
        order.setOrderDate(LocalDateTime.now());

        order.setSubTotal(preview.getSubTotal());
        order.setTaxAmount(BigDecimal.ZERO);
        order.setDiscountAmount(preview.getDiscountAmount());
        order.setShippingFee(preview.getShippingFee());

        order.setOrderType(preview.getOrderType());
        order.setOrderStatus("PENDING");

        // FIX: không dùng setPromotionId (Order không có field promotionId)
        if (preview.getAppliedPromotionId() != null) {
            Promotion promotion = promotionRepo.findById(preview.getAppliedPromotionId())
                    .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
            order.setPromotion(promotion);
        } else {
            order.setPromotion(null);
        }

        Order savedOrder = orderRepo.save(order);

        // 7) INSERT Shipping_Info
        ShippingInfo ship = new ShippingInfo();
        ship.setOrder(savedOrder);
        ship.setRecipientName(request.getRecipientName());
        ship.setRecipientPhone(request.getRecipientPhone());
        ship.setRecipientEmail(request.getRecipientEmail());

        ship.setRecipientAddress(buildFullAddress(address, user));

        ship.setProvinceCode(address != null ? address.getProvinceCode() : null);
        ship.setProvinceName(address != null ? address.getProvinceName() : null);
        ship.setDistrictCode(address != null ? address.getDistrictCode() : null);
        ship.setDistrictName(address != null ? address.getDistrictName() : null);
        ship.setWardCode(address != null ? address.getWardCode() : null);
        ship.setWardName(address != null ? address.getWardName() : null);

        ship.setShippingMethod("GHN");
        ship.setShippingFee(preview.getShippingFee());
        ship.setShippingStatus("PENDING");
        ship.setExpectedDeliveryAt(preview.getExpectedDeliveryAt());

        shippingInfoRepo.save(ship);

        // 8) INSERT Invoice
        Invoice invoice = new Invoice();
        invoice.setOrder(savedOrder);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setTotalAmount(preview.getTotalAmount());
        invoice.setStatus("UNPAID");
        invoiceRepo.save(invoice);

        // 9) INSERT Payment record(s)
        Payment createdPayment = null;

        if (plan.createDepositPayment) {
            // FIX: Payment() protected => dùng builder
            Payment dep = Payment.builder()
                    .order(savedOrder)
                    .paymentPurpose("DEPOSIT")
                    .createdAt(LocalDateTime.now())
                    .paymentDate(null)
                    .paymentMethod(plan.depositMethod)
                    .amount(plan.depositAmount)
                    .status("PENDING")
                    .build();
            paymentRepo.save(dep);

            createdPayment = dep; // payment cần redirect

            Payment rem = Payment.builder()
                    .order(savedOrder)
                    .paymentPurpose("REMAINING")
                    .createdAt(LocalDateTime.now())
                    .paymentDate(null)
                    .paymentMethod("COD")
                    .amount(plan.remainingAmount)
                    .status("PENDING")
                    .build();
            paymentRepo.save(rem);

        } else {
            Payment full = Payment.builder()
                    .order(savedOrder)
                    .paymentPurpose("FULL")
                    .createdAt(LocalDateTime.now())
                    .paymentDate(null)
                    .paymentMethod(plan.fullMethod)
                    .amount(plan.fullAmount)
                    .status("PENDING")
                    .build();
            paymentRepo.save(full);

            // nếu online thì đây là payment cần redirect
            if (!"COD".equalsIgnoreCase(plan.fullMethod)) {
                createdPayment = full;
            }
        }

        // 10) Load managed cart items rồi xóa bằng JPA remove (cascade/orphanRemoval sẽ chạy)
        var managedCartItems = cartItemRepo.findByCartItemIdIn(ids);
        if (managedCartItems.size() != ids.size()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        cartItemRepo.deleteAll(managedCartItems);

        // 12) nếu cần online redirect => tạo paymentUrl (hiện tại stub)
        String paymentUrl = null;
        boolean redirect = false;
        Long paymentId = null;

        if (createdPayment != null && !"COD".equalsIgnoreCase(createdPayment.getPaymentMethod())) {
            redirect = true;

            // FIX: entity Payment field paymentID => getter getPaymentID()
            paymentId = createdPayment.getPaymentID();

            // FIX: entity Order field orderID => getter getOrderID()
            paymentUrl = paymentGatewayService.createPaymentUrl(
                    createdPayment.getPaymentMethod(),
                    savedOrder.getOrderID(),
                    createdPayment.getPaymentID(),
                    createdPayment.getAmount()
            );
        }

        return CreateOrderResponse.builder()
                // FIX: use getOrderID()
                .orderId(savedOrder.getOrderID())
                .orderCode(savedOrder.getOrderCode())
                .orderStatus(savedOrder.getOrderStatus())

                .subTotal(preview.getSubTotal())
                .discountAmount(preview.getDiscountAmount())
                .shippingFee(preview.getShippingFee())
                .expectedDeliveryAt(preview.getExpectedDeliveryAt())
                .totalAmount(preview.getTotalAmount())

                .depositRequired(preview.isDepositRequired())
                .depositAmount(preview.getDepositAmount())
                .remainingAmount(preview.getRemainingAmount())

                .appliedPromotionId(preview.getAppliedPromotionId())

                .paymentRedirectRequired(redirect)
                .paymentUrl(paymentUrl)
                .paymentId(paymentId)
                .build();
    }

    private ShippingAddressRequest resolveAddress(ShippingAddressRequest reqAddr, User user) {
        if (reqAddr != null && reqAddr.getDistrictCode() != null && reqAddr.getWardCode() != null) {
            return reqAddr;
        }

        // dùng default codes từ user
        if (user.getDistrictCode() == null || user.getWardCode() == null) {
            // chưa có default codes => preview sẽ shipFee=0, FE buộc user chọn địa chỉ mới
            return null;
        }

        ShippingAddressRequest a = new ShippingAddressRequest();
        a.setStreet(null);
        a.setProvinceCode(user.getProvinceCode());
        a.setProvinceName(user.getProvinceName());
        a.setDistrictCode(user.getDistrictCode());
        a.setDistrictName(user.getDistrictName());
        a.setWardCode(user.getWardCode());
        a.setWardName(user.getWardName());
        return a;
    }

    private String buildFullAddress(ShippingAddressRequest address, User user) {
        if (address == null) return user.getAddress();

        String street = address.getStreet() != null ? address.getStreet().trim() : null;
        if (street == null || street.isEmpty()) return user.getAddress();

        return street + ", " + address.getWardName() + ", " + address.getDistrictName() + ", " + address.getProvinceName();
    }

    private String generateOrderCode() {
        return "ORD-" + LocalDateTime.now().toLocalDate() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Convert rule UI payment => plan tạo Payment records.
     * - Nếu không cần cọc:
     *    COD => 1 record FULL COD PENDING
     *    VNPAY/MOMO => 1 record FULL online PENDING (redirect)
     * - Nếu cần cọc:
     *    paymentMethod=COD => DEPOSIT online + REMAINING COD
     *    paymentMethod=VNPAY/MOMO:
     *       payStrategy=FULL => FULL online
     *       payStrategy=DEPOSIT => DEPOSIT online + REMAINING COD
     */
    private PaymentPlan buildPaymentPlan(CheckoutPreviewResponse preview, CreateOrderRequest req) {
        boolean depositRequired = preview.isDepositRequired();

        String method = req.getPaymentMethod(); // COD/VNPAY/MOMO
        String payStrategy = req.getPayStrategy(); // FULL/DEPOSIT

        if (!depositRequired) {
            return PaymentPlan.full(method, preview.getTotalAmount());
        }

        BigDecimal dep = preview.getDepositAmount();
        BigDecimal rem = preview.getRemainingAmount();

        if ("COD".equalsIgnoreCase(method)) {
            // COD main but deposit must be online
            if (req.getDepositPaymentMethod() == null) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            return PaymentPlan.deposit(req.getDepositPaymentMethod(), dep, rem);
        }

        // online main
        if ("FULL".equalsIgnoreCase(payStrategy)) {
            return PaymentPlan.full(method, preview.getTotalAmount());
        }

        // default: DEPOSIT
        return PaymentPlan.deposit(method, dep, rem);
    }

    private static class PaymentPlan {
        boolean createDepositPayment;
        String depositMethod;
        BigDecimal depositAmount;
        BigDecimal remainingAmount;

        String fullMethod;
        BigDecimal fullAmount;

        static PaymentPlan deposit(String depositMethod, BigDecimal depositAmount, BigDecimal remainingAmount) {
            PaymentPlan p = new PaymentPlan();
            p.createDepositPayment = true;
            p.depositMethod = depositMethod;
            p.depositAmount = depositAmount;
            p.remainingAmount = remainingAmount;
            return p;
        }

        static PaymentPlan full(String fullMethod, BigDecimal fullAmount) {
            PaymentPlan p = new PaymentPlan();
            p.createDepositPayment = false;
            p.fullMethod = fullMethod;
            p.fullAmount = fullAmount;
            return p;
        }
    }
}