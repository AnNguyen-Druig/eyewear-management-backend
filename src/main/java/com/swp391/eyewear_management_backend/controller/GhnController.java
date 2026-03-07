package com.swp391.eyewear_management_backend.controller;

import com.swp391.eyewear_management_backend.dto.response.ApiResponse;
import com.swp391.eyewear_management_backend.dto.response.ghn.DistrictResponse;
import com.swp391.eyewear_management_backend.dto.response.ghn.ProvinceResponse;
import com.swp391.eyewear_management_backend.dto.response.ghn.WardResponse;
import com.swp391.eyewear_management_backend.service.GhnLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ghn")
public class GhnController {

    private final GhnLocationService ghnLocationService;

    @GetMapping("/provinces")
    public ApiResponse<List<ProvinceResponse>> provinces() {
        return ApiResponse.<List<ProvinceResponse>>builder()
                .message("OK")
                .result(ghnLocationService.getProvinces())
                .build();
    }

    @GetMapping("/districts")
    public ApiResponse<List<DistrictResponse>> districts(@RequestParam Integer provinceId) {
        return ApiResponse.<List<DistrictResponse>>builder()
                .message("OK")
                .result(ghnLocationService.getDistricts(provinceId))
                .build();
    }

    @GetMapping("/wards")
    public ApiResponse<List<WardResponse>> wards(@RequestParam Integer districtId) {
        return ApiResponse.<List<WardResponse>>builder()
                .message("OK")
                .result(ghnLocationService.getWards(districtId))
                .build();
    }
}