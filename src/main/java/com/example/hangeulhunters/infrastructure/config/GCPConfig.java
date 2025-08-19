package com.example.hangeulhunters.infrastructure.config;

import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Google Cloud Platform 관련 설정
 */
@Configuration
public class GCPConfig {

    /**
     * Google Cloud Text-to-Speech 클라이언트 빈
     */
    @Bean
    public TextToSpeechClient textToSpeechClient() {
        try {
            return TextToSpeechClient.create();
        } catch (IOException e) {
            throw new RuntimeException("Unable to import Google Cloud Text-to-Speech client.", e);
        }
    }
    
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
            private String languageCode = "ko-KR";
            private String defaultVoiceName = "ko-KR-Standard-A";
            private String defaultMaleVoiceName = "ko-KR-Standard-C";
            private String defaultFemaleVoiceName = "ko-KR-Standard-A";
        }
    }
}