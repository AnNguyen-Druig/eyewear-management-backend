package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.response.ReturnExchangeItemResponse;
import com.swp391.eyewear_management_backend.dto.response.ReturnExchangeResponse;
import com.swp391.eyewear_management_backend.entity.ReturnExchange;
import com.swp391.eyewear_management_backend.entity.ReturnExchangeItem; // Đảm bảo import đúng entity con
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReturnExchangeMapper {

    
    @Mapping(source = "returnExchangeID", target = "returnExchangeID")
    @Mapping(source = "returnCode", target = "returnCode")
    @Mapping(source = "order.orderID", target = "orderId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "returnType", target = "returnType")
    @Mapping(source = "requestScope", target = "requestScope")
    @Mapping(source = "requestNote", target = "requestNote")
    @Mapping(source = "returnReason", target = "returnReason")
    @Mapping(source = "refundAmount", target = "refundAmount")
    @Mapping(source = "refundMethod", target = "refundMethod")
    @Mapping(source = "refundAccountNumber", target = "refundAccountNumber")
    @Mapping(source = "refundAccountName", target = "refundAccountName")
    @Mapping(source = "refundReferenceCode", target = "refundReferenceCode")
    @Mapping(source = "staffRefundEvidenceUrl", target = "staffRefundEvidenceUrl")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "requestDate", target = "requestDate")
    @Mapping(source = "approvedDate", target = "approvedDate")
    @Mapping(source = "approvedBy.userId", target = "approvedById")
    @Mapping(source = "processedDate", target = "processedDate")
    @Mapping(source = "processedBy.userId", target = "processedById")
    @Mapping(source = "rejectReason", target = "rejectReason")
    ReturnExchangeResponse toReturnExchangeResponse(ReturnExchange returnExchange);

    // --- 2. Map cho các Item con bên trong ---
    @Mapping(source = "orderDetail.orderDetailID", target = "orderDetailId") // Giả sử entity ReturnExchangeItem có object OrderDetail
    // Các trường như quantity, note, itemReason trùng tên sẽ tự động map
    ReturnExchangeItemResponse toReturnExchangeItemResponse(ReturnExchangeItem item);
}
