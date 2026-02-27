package com.example.hangeulhunters.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * GCP Cloud Storage 설정
 */
@Configuration
public class GcsConfig {

    @Value("${gcs.credentials-json:#{null}}")
    private String credentialsJson;

    private final ResourceLoader resourceLoader;

    public GcsConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * GCS Storage 클라이언트 생성
     * credentials-json 이 설정된 경우 서비스 계정 키 파일을 사용하고,
     * 설정되지 않은 경우 Application Default Credentials 를 사용합니다.
     *
     * credentials-json 값에는 아래 형식을 지원합니다:
     * - classpath:credential/noonchi_iam_key.json (resources 폴더 내)
     * - /절대/경로/key.json (파일 시스템 절대 경로)
     */
    @Bean
    public Storage storage() throws IOException {
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            Resource resource = resourceLoader.getResource(credentialsJson);
            if (resource.exists()) {
                try (InputStream stream = resource.getInputStream()) {
                    GoogleCredentials credentials = GoogleCredentials
                            .fromStream(stream)
                            .createScoped("https://www.googleapis.com/auth/cloud-platform");
                    return StorageOptions.newBuilder()
                            .setCredentials(credentials)
                            .build()
                            .getService();
                }
            }
            // 파일이 없으면 ADC로 fallback
        }
        // Application Default Credentials (환경변수 GOOGLE_APPLICATION_CREDENTIALS 또는
        // gcloud 인증)
        return StorageOptions.getDefaultInstance().getService();
    }
}
