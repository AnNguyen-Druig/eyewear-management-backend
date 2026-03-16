package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.request.ReturnExchangeItemRequest;
import com.swp391.eyewear_management_backend.dto.request.ReturnExchangeRequest;
import com.swp391.eyewear_management_backend.dto.response.ReturnExchangeResponse;
import com.swp391.eyewear_management_backend.entity.*;
import com.swp391.eyewear_management_backend.exception.AppException;
import com.swp391.eyewear_management_backend.exception.ErrorCode;
import com.swp391.eyewear_management_backend.mapper.ReturnExchangeMapper;
import com.swp391.eyewear_management_backend.repository.OrderDetailRepo;
import com.swp391.eyewear_management_backend.repository.OrderRepo;
import com.swp391.eyewear_management_backend.repository.ReturnExchangeRepo;
import com.swp391.eyewear_management_backend.repository.UserRepo;
import com.swp391.eyewear_management_backend.service.ReturnExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReturnExchangeServiceImpl implements ReturnExchangeService {

    @Autowired
    private ReturnExchangeRepo returnExchangeRepository;

    @Autowired
    private OrderDetailRepo orderDetailRepository;

    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ReturnExchangeMapper returnExchangeMapper;

    /**
     * Lấy user hiện tại từ security context
     */
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
     * Tạo mã đổi trả tự động
     */
    private String generateReturnCode() {
        String code;
        do {
            code = "RX" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        } while (returnExchangeRepository.findByReturnCode(code).isPresent());
        return code;
    }

    @Override
    public ReturnExchangeResponse createReturnExchange(ReturnExchangeRequest request) {
        User currentUser = getCurrentUser();
        Order order = resolveOrder(request);
        String returnType = normalizeReturnType(request.getReturnType());
        String requestScope = normalizeRequestScope(request.getRequestScope(), returnType);

        if ("REFUND".equals(returnType)
                && returnExchangeRepository.existsByOrder_OrderIDAndReturnTypeAndRequestScopeAndStatusIn(
                order.getOrderID(), returnType, requestScope, List.of("PENDING", "APPROVED"))) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        ReturnExchange returnExchange = new ReturnExchange();
        returnExchange.setOrder(order);
        returnExchange.setUser(currentUser);
        returnExchange.setReturnCode(generateReturnCode());
        returnExchange.setRequestDate(LocalDateTime.now());
        returnExchange.setReturnType(returnType);
        returnExchange.setRequestScope(requestScope);
        returnExchange.setRequestNote(trimToNull(request.getRequestNote()));
        returnExchange.setReturnReason(request.getReturnReason());
        returnExchange.setCustomerEvidenceUrl(trimToNull(request.getCustomerEvidenceUrl()));
        returnExchange.setRefundAmount(request.getRefundAmount());
        returnExchange.setRefundMethod(trimToNull(request.getRefundMethod()));
        returnExchange.setRefundAccountNumber(trimToNull(request.getRefundAccountNumber()));
        returnExchange.setRefundAccountName(trimToNull(request.getRefundAccountName()));
        returnExchange.setRefundReferenceCode(trimToNull(request.getRefundReferenceCode()));
        returnExchange.setStaffRefundEvidenceUrl(trimToNull(request.getStaffRefundEvidenceUrl()));
        returnExchange.setStatus("PENDING");
        returnExchange.setReturnExchangeItems(buildReturnExchangeItems(request, order, returnExchange, requestScope));

        ReturnExchange savedReturnExchange = returnExchangeRepository.save(returnExchange);
        return returnExchangeMapper.toReturnExchangeResponse(savedReturnExchange);
    }

    @Override
    public ReturnExchangeResponse getReturnExchangeById(Long returnExchangeId) {
        ReturnExchange returnExchange = returnExchangeRepository.findById(returnExchangeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp
        return returnExchangeMapper.toReturnExchangeResponse(returnExchange);
    }

    @Override
    public ReturnExchangeResponse getReturnExchangeByCode(String returnCode) {
        ReturnExchange returnExchange = returnExchangeRepository.findByReturnCode(returnCode)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp
        return returnExchangeMapper.toReturnExchangeResponse(returnExchange);
    }

    @Override
    public List<ReturnExchangeResponse> getMyReturnExchanges() {
        User currentUser = getCurrentUser();
        List<ReturnExchange> returnExchanges = returnExchangeRepository.findByUser_UserId(currentUser.getUserId());
        return returnExchanges.stream()
                .map(returnExchangeMapper::toReturnExchangeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnExchangeResponse> getAllReturnExchanges() {
        List<ReturnExchange> returnExchanges = returnExchangeRepository.findAll();
        return returnExchanges.stream()
                .map(returnExchangeMapper::toReturnExchangeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnExchangeResponse> getReturnExchangesByStatus(String status) {
        List<ReturnExchange> returnExchanges = returnExchangeRepository.findByStatus(status);
        return returnExchanges.stream()
                .map(returnExchangeMapper::toReturnExchangeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnExchangeResponse approveReturnExchange(Long returnExchangeId) {
        User approver = getCurrentUser();
        ReturnExchange returnExchange = returnExchangeRepository.findById(returnExchangeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp

        returnExchange.setApprovedBy(approver);
        returnExchange.setApprovedDate(LocalDateTime.now());
        returnExchange.setStatus("APPROVED");

        ReturnExchange updatedReturnExchange = returnExchangeRepository.save(returnExchange);
        return returnExchangeMapper.toReturnExchangeResponse(updatedReturnExchange);
    }

    @Override
    public ReturnExchangeResponse rejectReturnExchange(Long returnExchangeId, String rejectReason) {
        User approver = getCurrentUser();
        ReturnExchange returnExchange = returnExchangeRepository.findById(returnExchangeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp

        returnExchange.setApprovedBy(approver);
        returnExchange.setApprovedDate(LocalDateTime.now());
        returnExchange.setStatus("REJECTED");
        returnExchange.setRejectReason(rejectReason);

        ReturnExchange updatedReturnExchange = returnExchangeRepository.save(returnExchange);
        return returnExchangeMapper.toReturnExchangeResponse(updatedReturnExchange);
    }

    @Override
    public ReturnExchangeResponse updateReturnExchange(Long returnExchangeId, ReturnExchangeRequest request) {
        ReturnExchange returnExchange = returnExchangeRepository.findById(returnExchangeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp

        // Chỉ có thể cập nhật nếu status là PENDING
        if (!returnExchange.getStatus().equals("PENDING")) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // Thay bằng lỗi phù hợp
        }

        if (request.getRequestNote() != null) {
            returnExchange.setRequestNote(trimToNull(request.getRequestNote()));
        }
        if (request.getReturnReason() != null) {
            returnExchange.setReturnReason(request.getReturnReason());
        }
        if (request.getCustomerEvidenceUrl() != null) {
            returnExchange.setCustomerEvidenceUrl(trimToNull(request.getCustomerEvidenceUrl()));
        }
        if (request.getRefundAmount() != null) {
            returnExchange.setRefundAmount(request.getRefundAmount());
        }
        if (request.getRefundMethod() != null) {
            returnExchange.setRefundMethod(trimToNull(request.getRefundMethod()));
        }
        if (request.getRefundAccountNumber() != null) {
            returnExchange.setRefundAccountNumber(trimToNull(request.getRefundAccountNumber()));
        }
        if (request.getRefundAccountName() != null) {
            returnExchange.setRefundAccountName(trimToNull(request.getRefundAccountName()));
        }
        if (request.getRefundReferenceCode() != null) {
            returnExchange.setRefundReferenceCode(trimToNull(request.getRefundReferenceCode()));
        }
        if (request.getStaffRefundEvidenceUrl() != null) {
            returnExchange.setStaffRefundEvidenceUrl(trimToNull(request.getStaffRefundEvidenceUrl()));
        }
        if (request.getItems() != null) {
            returnExchange.getReturnExchangeItems().clear();
            returnExchange.getReturnExchangeItems()
                    .addAll(buildReturnExchangeItems(request, returnExchange.getOrder(), returnExchange, returnExchange.getRequestScope()));
        }

        ReturnExchange updatedReturnExchange = returnExchangeRepository.save(returnExchange);
        return returnExchangeMapper.toReturnExchangeResponse(updatedReturnExchange);
    }

    @Override
    public void deleteReturnExchange(Long returnExchangeId) {
        ReturnExchange returnExchange = returnExchangeRepository.findById(returnExchangeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Thay bằng lỗi phù hợp

        // Chỉ có thể xóa nếu status là PENDING
        if (!returnExchange.getStatus().equals("PENDING")) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // Thay bằng lỗi phù hợp
        }

        returnExchangeRepository.delete(returnExchange);
    }

    private Order resolveOrder(ReturnExchangeRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (request.getOrderId() != null) {
            return orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        }
        if (request.getOrderDetailId() != null) {
            OrderDetail orderDetail = orderDetailRepository.findById(request.getOrderDetailId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));
            return orderDetail.getOrder();
        }
        throw new AppException(ErrorCode.INVALID_REQUEST);
    }

    private String normalizeReturnType(String returnType) {
        String normalized = trimToNull(returnType);
        if (normalized == null) {
            return "REFUND";
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private String normalizeRequestScope(String requestScope, String returnType) {
        String normalized = trimToNull(requestScope);
        if (normalized == null) {
            return "REFUND".equals(returnType) ? "ORDER" : "ITEM";
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private List<ReturnExchangeItem> buildReturnExchangeItems(
            ReturnExchangeRequest request,
            Order order,
            ReturnExchange returnExchange,
            String requestScope
    ) {
        if (!"ITEM".equals(requestScope)) {
            return List.of();
        }
        List<ReturnExchangeItemRequest> items = request.getItems();
        if (items == null || items.isEmpty()) {
            if (request.getOrderDetailId() == null) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            items = List.of(ReturnExchangeItemRequest.builder()
                    .orderDetailId(request.getOrderDetailId())
                    .quantity(1)
                    .build());
        }

        List<ReturnExchangeItem> result = new ArrayList<>();
        for (ReturnExchangeItemRequest itemRequest : items) {
            if (itemRequest.getOrderDetailId() == null || itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            OrderDetail orderDetail = orderDetailRepository.findById(itemRequest.getOrderDetailId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));
            if (orderDetail.getOrder() == null || !order.getOrderID().equals(orderDetail.getOrder().getOrderID())) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }
            ReturnExchangeItem item = new ReturnExchangeItem();
            item.setReturnExchange(returnExchange);
            item.setOrderDetail(orderDetail);
            item.setQuantity(itemRequest.getQuantity());
            item.setItemReason(trimToNull(itemRequest.getItemReason()));
            item.setNote(trimToNull(itemRequest.getNote()));
            result.add(item);
        }
        return result;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
