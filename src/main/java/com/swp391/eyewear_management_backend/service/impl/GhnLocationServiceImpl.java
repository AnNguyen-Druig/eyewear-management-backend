package com.swp391.eyewear_management_backend.service.impl;

import com.swp391.eyewear_management_backend.dto.response.ghn.*;
import com.swp391.eyewear_management_backend.integration.ghn.GhnLocationClient;
import com.swp391.eyewear_management_backend.mapper.GhnLocationMapper;
import com.swp391.eyewear_management_backend.service.GhnLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GhnLocationServiceImpl implements GhnLocationService {

    private final GhnLocationClient ghnLocationClient;

    @Override
    @Cacheable(cacheNames = "ghnProvinces")
    public List<ProvinceResponse> getProvinces() {
        return ghnLocationClient.getProvinces()
                .stream()
                .map(GhnLocationMapper::toProvince)
                .toList();
    }

    @Override
    @Cacheable(cacheNames = "ghnDistricts", key = "#provinceId")
    public List<DistrictResponse> getDistricts(Integer provinceId) {
        return ghnLocationClient.getDistricts(provinceId)
                .stream()
                .map(GhnLocationMapper::toDistrict)
                .toList();
    }

    @Override
    @Cacheable(cacheNames = "ghnWards", key = "#districtId")
    public List<WardResponse> getWards(Integer districtId) {
        return ghnLocationClient.getWards(districtId)
                .stream()
                .map(GhnLocationMapper::toWard)
                .toList();
    }
}
