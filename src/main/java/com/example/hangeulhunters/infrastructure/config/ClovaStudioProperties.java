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
     * 예: https://clovastudio.apigw.ntruss.com
     */
    private String baseUrl;

    /**
     * 대화 생성(챗) 엔드포인트 경로
     * 예: /v1/api/chat
     */
    private String chatPath;

    /**
     * 평가 엔드포인트 경로(사용 시)
     */
    private String evalPath;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 프로젝트/엔진 등 추가 파라미터가 필요한 경우
     */
    private String projectId;
    private String model;
}