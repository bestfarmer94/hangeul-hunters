package com.example.hangeulhunters.infrastructure.service.gcs;

import com.example.hangeulhunters.infrastructure.service.gcs.dto.PresignedUrlDto;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * GCS(Google Cloud Storage) 서비스
 * 파일 업로드 및 관리를 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class GcsService {

    private final Storage storage;

    @Value("${gcs.bucket}")
    private String bucketName;

    private static final String TEMP_FOLDER = "temp/";
    private static final long SIGNED_URL_DURATION = 1; // 1시간

    /**
     * 임시 파일 업로드를 위한 Signed URL 생성 (PUT 방식)
     *
     * @param fileType      파일 타입 (image/jpeg, image/png 등)
     * @param fileExtension 파일 확장자 (jpg, png 등)
     * @return Signed URL 정보 (URL, 키)
     */
    public PresignedUrlDto generatePresignedUrl(String fileType, String fileExtension) {
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String key = TEMP_FOLDER + fileName;

        String contentType = getContentType(fileExtension);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, key))
                .setContentType(contentType)
                .build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                SIGNED_URL_DURATION,
                TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature());

        return PresignedUrlDto.builder()
                .url(signedUrl.toString())
                .build();
    }

    /**
     * 임시 파일을 지정된 폴더로 이동
     *
     * @param destinationPath 목적 경로
     * @param fileUrl         파일 URL
     * @return 이동된 파일의 공개 URL
     */
    public String moveFile(String destinationPath, String fileUrl) {
        String sourceKey = extractKeyFromUrl(fileUrl);

        if (!sourceKey.startsWith(TEMP_FOLDER)) {
            // temp 폴더에 없는 경우, 이미 영구 저장소에 있는 파일로 간주하고 그대로 반환
            return fileUrl;
        }

        String fileName = sourceKey.substring(TEMP_FOLDER.length());
        String destinationKey = destinationPath + fileName;

        BlobId source = BlobId.of(bucketName, sourceKey);
        BlobId destination = BlobId.of(bucketName, destinationKey);

        // 복사
        storage.copy(Storage.CopyRequest.newBuilder()
                .setSource(source)
                .setTarget(destination)
                .build());

        // 원본 삭제
        storage.delete(source);

        return buildPublicUrl(destinationKey);
    }

    /**
     * 임시 폴더에 파일을 업로드하고 URL을 반환
     *
     * @param data          파일 데이터 (byte array)
     * @param fileExtension 파일 확장자
     * @return 업로드된 파일의 공개 URL
     */
    public String uploadTempFile(byte[] data, String fileExtension) {
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String key = TEMP_FOLDER + fileName;

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, key))
                .setContentType(getContentType(fileExtension))
                .build();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            storage.createFrom(blobInfo, inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to GCS", e);
        }

        return buildPublicUrl(key);
    }

    /**
     * GCS 공개 URL 빌드
     * (버킷이 공개 설정되어 있어야 합니다)
     */
    private String buildPublicUrl(String key) {
        return String.format("https://storage.cloud.google.com/%s/%s", bucketName, key);
    }

    /**
     * URL 에서 GCS 객체 키 추출
     *
     * @param url GCS 객체 URL (https://storage.cloud.google.com/{bucket}/{key})
     * @return 객체 키
     */
    private String extractKeyFromUrl(String url) {
        // https://storage.cloud.google.com/{bucketName}/{key}
        String prefix = "https://storage.cloud.google.com/" + bucketName + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }

        // Signed URL 형식: https://storage.cloud.google.com/{bucket}/{key}?X-Goog-...
        if (url.contains("storage.cloud.google.com/" + bucketName + "/")) {
            int start = url.indexOf("storage.cloud.google.com/" + bucketName + "/")
                    + ("storage.cloud.google.com/" + bucketName + "/").length();
            int end = url.contains("?") ? url.indexOf("?") : url.length();
            return url.substring(start, end);
        }

        throw new IllegalArgumentException("Invalid GCS URL format: " + url);
    }

    private String getContentType(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "flac" -> "audio/flac";
            case "pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }
}
