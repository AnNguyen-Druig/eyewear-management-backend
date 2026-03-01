package com.swp391.eyewear_management_backend.integration.ghn;

import com.swp391.eyewear_management_backend.dto.response.GhnApiResponse;
import com.swp391.eyewear_management_backend.dto.response.ghn.GhnDistrictRaw;
import com.swp391.eyewear_management_backend.dto.response.ghn.GhnProvinceRaw;
import com.swp391.eyewear_management_backend.dto.response.ghn.GhnWardRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GhnLocationClient {

    private final RestClient ghnResClient;

    public List<GhnProvinceRaw> getProvinces() {
        var typeRef = new ParameterizedTypeReference<GhnApiResponse<List<GhnProvinceRaw>>>() {};
        GhnApiResponse<List<GhnProvinceRaw>> resp = ghnResClient.get()
                .uri("/master-data/province")
                .retrieve()
                .body(typeRef);

        return resp != null ? resp.getData() : null;
    }

    public List<GhnDistrictRaw> getDistricts(Integer provinceId) {
        var typeRef = new ParameterizedTypeReference<GhnApiResponse<List<GhnDistrictRaw>>>() {};
        GhnApiResponse<List<GhnDistrictRaw>> resp = ghnResClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/master-data/district")
                        .queryParam("province_id", provinceId)
                        .build())
                .retrieve()
                .body(typeRef);

        return resp != null ? resp.getData() : List.of();
    }

    public List<GhnWardRaw> getWards(Integer districtId) {
        var typeRef = new ParameterizedTypeReference<GhnApiResponse<List<GhnWardRaw>>>() {};
        GhnApiResponse<List<GhnWardRaw>> resp = ghnResClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/master-data/ward")
                        .queryParam("district_id", districtId)
                        .build())
                .retrieve()
                .body(typeRef);

        return resp != null ? resp.getData() : List.of();
    }
}
