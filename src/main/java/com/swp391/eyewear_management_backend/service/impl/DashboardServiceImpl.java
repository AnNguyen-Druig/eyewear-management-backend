package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.response.*;
import com.swp391.eyewear_management_backend.mapper.DashboardMapper;
import com.swp391.eyewear_management_backend.repository.OrderDetailRepo;
import com.swp391.eyewear_management_backend.repository.OrderRepo;
import com.swp391.eyewear_management_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepo orderRepository;
    private final OrderDetailRepo orderDetailRepository;
    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardResponse getDashboardStatistics(LocalDate startDateInput, LocalDate endDateInput) {

        // =========================================================================
        // 1. XỬ LÝ 2 NGÀY INPUT TỪ FRONTEND
        // =========================================================================
        // Ngày kết thúc (Nếu FE truyền thì lấy 23:59:59 của ngày đó, nếu không thì lấy Now)
        LocalDateTime endDateTime = (endDateInput != null)
                ? endDateInput.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        // Ngày bắt đầu (Nếu FE truyền thì lấy 00:00:00 của ngày đó, nếu không thì lấy lùi 7 ngày)
        LocalDateTime startDateTime = (startDateInput != null)
                ? startDateInput.atStartOfDay()
                : endDateTime.minusDays(6).with(LocalTime.MIN);

        // =========================================================================
        // 2. TÍNH CÁC MỐC THỜI GIAN CHO Ô SUMMARY
        // (Tính lùi lại từ endDateTime để các số liệu luôn chuẩn xác với ngày FE chọn)
        // =========================================================================
        LocalDateTime startOfDay = endDateTime.with(LocalTime.MIN);
        LocalDateTime startOfWeek = endDateTime.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime startOfMonth = endDateTime.withDayOfMonth(1).with(LocalTime.MIN);

        // 3. Tính toán Summary thực tế
        BigDecimal revDay = orderRepository.calculateRevenueBetween(startOfDay, endDateTime);
        BigDecimal revWeek = orderRepository.calculateRevenueBetween(startOfWeek, endDateTime);
        BigDecimal revMonth = orderRepository.calculateRevenueBetween(startOfMonth, endDateTime);

        // Trạng thái PENDING thì đếm tổng hiện có, không phụ thuộc ngày
        int pendingOrders = orderRepository.countByOrderStatus("PENDING");
        // Đơn hoàn thành thì đếm trong khoảng tháng đó
        int completedOrdersMonth = orderRepository.countByOrderStatusAndOrderDateBetween("COMPLETED", startOfMonth, endDateTime);

        SummaryResponse summary = SummaryResponse.builder()
                .revenueDay(revDay != null ? revDay.longValue() : 0L)
                .revenueWeek(revWeek != null ? revWeek.longValue() : 0L)
                .revenueMonth(revMonth != null ? revMonth.longValue() : 0L)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrdersMonth)
                .build();

        // =========================================================================
        // 4. LẤY DỮ LIỆU CÁC BIỂU ĐỒ VÀ TOP SẢN PHẨM
        // =========================================================================

        // Biểu đồ doanh thu bắt đầu chạy từ startDateTime (Ngày FE truyền vào)
        List<RevenueChartResponse> revenueChart = orderRepository.getRevenueChartNative(startDateTime)
                .stream()
                .map(dashboardMapper::toRevenueDto)
                .collect(Collectors.toList());

        // Biểu đồ trạng thái đơn hàng
        List<OrderStatusChartResponse> orderStatusChart = orderRepository.getOrderStatusChart()
                .stream()
                .map(dashboardMapper::toOrderStatusDto)
                .collect(Collectors.toList());

        // Top 3 Sản phẩm bán chạy
        List<TopProductResponse> topProducts = orderDetailRepository.getTopSellingProducts(PageRequest.of(0, 3))
                .stream()
                .map(dashboardMapper::toTopProductDto)
                .collect(Collectors.toList());

        // Đóng gói DTO tổng
        return DashboardResponse.builder()
                .summary(summary)
                .revenueChart(revenueChart)
                .orderStatusChart(orderStatusChart)
                .topProducts(topProducts)
                .build();
    }
}