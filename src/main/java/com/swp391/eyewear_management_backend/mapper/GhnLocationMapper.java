package com.swp391.eyewear_management_backend.mapper;

import com.swp391.eyewear_management_backend.dto.response.ghn.*;

public class GhnLocationMapper {

    public static ProvinceResponse toProvince(GhnProvinceRaw provinceRaw) {
        return new ProvinceResponse(provinceRaw.getProvinceId(), provinceRaw.getProvinceName());
    }

    public static DistrictResponse toDistrict(GhnDistrictRaw districtRaw) {
        return new DistrictResponse(districtRaw.getDistrictId(), districtRaw.getDistrictName(), districtRaw.getProvinceId());
    }

    public static WardResponse toWard(GhnWardRaw wardRaw) {
        return new WardResponse(wardRaw.getWardCode(), wardRaw.getWardName(),wardRaw.getDistrictId());
    }
}
