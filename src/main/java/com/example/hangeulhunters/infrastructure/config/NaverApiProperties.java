package com.example.hangeulhunters.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.naver-api")
public class NaverApiProperties {
    private ClovaStudio clovaStudio;
    private Papago papago;

    @Data
    public static class ClovaStudio {
        private String baseUrl;
        private String tuningModelPath;
        private String commonModelPath;
        private String apiKey;
    }

    @Data
    public static class Papago {
        private String baseUrl;
        private String translationPath;
        private String clientId;
        private String clientSecret;
    }
}