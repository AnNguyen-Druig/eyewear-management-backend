package com.swp391.eyewear_management_backend;

import com.swp391.eyewear_management_backend.config.ghn.GhnProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(GhnProperties.class)
public class EyewearManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EyewearManagementBackendApplication.class, args);
    }

}
