package com.swp391.eyewear_management_backend.config.ghn;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ghn")
public class GhnProperties {
    private String baseUrl;
    private String token;
    private Long shopId;
}