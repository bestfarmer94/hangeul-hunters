package com.example.hangeulhunters.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "cloud.naver.clovastudio")
public class ClovaStudioProperties {

    /**
     * CLOVA Studio API Base URL
     */
    private String baseUrl;

    /**
     * 튜닝 모델 엔드포인트 경로 (대화 모델)
     */
    private String tuningModelPath;

    /**
     * 공통 모델 엔드포인트 경로
     */
    private String commonModelPath;

    /**
     * API Key
     */
    private String apiKey;
}