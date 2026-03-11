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
                .build();
    }

    public List<Map<String, Object>> availableServices(int toDistrictId) {
        Map<String, Object> body = Map.of(
                "shop_id", props.getShopId(),
                "from_district", props.getFromDistrictId(),
                "to_district", toDistrictId
        );

        var resp = client().post()
                .uri("/v2/shipping-order/available-services")
                .header("Token", props.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return (List<Map<String, Object>>) resp.get("data");
    }

    public Map<String, Object> createOrder(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/create")
                .header("Token", props.getToken())
                .header("ShopId", String.valueOf(props.getShopId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> detailByClientCode(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/detail-by-client-code")
                .header("Token", props.getToken())
                .header("ShopId", String.valueOf(props.getShopId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> fee(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/fee")
                .header("Token", props.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> leadtime(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/leadtime")
                .header("Token", props.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    public Map<String, Object> detail(Map<String, Object> body) {
        return client().post()
                .uri("/v2/shipping-order/detail")
                .header("Token", props.getToken())
                .header("ShopId", String.valueOf(props.getShopId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}