package com.swp391.eyewear_management_backend.config.ocr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.ocr")
public class OcrProperties {
    private boolean enabled = false;
    private String endpoint = "https://api.ocr.space/parse/image";
    private String apiKey = "";
    private String language = "eng";
    private int timeoutSeconds = 20;
    private long maxFileSizeBytes = 5_242_880;
}
