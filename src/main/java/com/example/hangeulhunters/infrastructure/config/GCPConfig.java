package com.example.hangeulhunters.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Google Cloud Platform 관련 설정
 */
public class GCPConfig {

    /**
     * Google Cloud 설정 속성
     */
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "cloud.google-api")
    public static class GCPProperties {
        private TTS tts;

        @Data
        public static class TTS {
            private String apiKey;
            private String baseUrl;
            private String synthesizePath;
            private String languageCode;
            private String defaultVoiceName;
        }
    }
}