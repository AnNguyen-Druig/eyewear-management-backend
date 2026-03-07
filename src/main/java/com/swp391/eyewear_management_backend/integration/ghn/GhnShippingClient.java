package com.swp391.eyewear_management_backend.integration.ghn;


import com.swp391.eyewear_management_backend.config.ghn.GhnProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GhnShippingClient {

    private final GhnProperties props;

    private RestClient client() {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("Token", props.getToken())
                .defaultHeader("ShopId", String.valueOf(props.getShopId()))
                .build();
    }

    // GHN: /v2/shipping-order/available-services
    public List<Map<String, Object>> availableServices(int toDistrictId) {
        Map<String, Object> body = Map.of(
                "shop_id", props.getShopId(),
                "from_district", props.getFromDistrictId(),
                "to_district", toDistrictId
        );

        var resp = client().post()
                .uri("/v2/shipping-order/available-services")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        // resp: {code, message, data:[{service_id, short_name, service_type_id}, ...]}
        return (List<Map<String, Object>>) resp.get("data");
    }

    // GHN: /v2/shipping-order/fee
    public Map<String, Object> fee(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/fee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    // GHN: /v2/shipping-order/leadtime
    public Map<String, Object> leadtime(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/leadtime")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}