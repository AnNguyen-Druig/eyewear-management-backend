package com.swp391.eyewear_management_backend.service;

import com.swp391.eyewear_management_backend.dto.response.ghn.*;

import java.util.List;

public interface GhnLocationService {

    List<ProvinceResponse> getProvinces();
    List<DistrictResponse> getDistricts(Integer provinceId);
    List<WardResponse> getWards(Integer districtId);
}
