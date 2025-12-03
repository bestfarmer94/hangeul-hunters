package com.example.hangeulhunters.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.noonchi-ai")
public class NoonchiAiProperties {
    private String baseUrl;
    private String apiKey;
    private Endpoints endpoints;

    @Data
    public static class Endpoints {
        private String rolePlayingStart;
        private String rolePlayingChat;
        private String interviewStart;
        private String interviewChat;
        private String translation;
        private String report;
    }
}
