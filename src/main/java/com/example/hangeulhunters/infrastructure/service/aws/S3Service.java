package com.example.hangeulhunters.infrastructure.service.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.hangeulhunters.infrastructure.service.aws.dto.PresignedUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * S3 서비스
 * 파일 업로드 및 관리를 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.bucket}")
    private String bucketName;

    private static final String TEMP_FOLDER = "temp/";
    private static final long PRESIGNED_URL_EXPIRATION = 1000 * 60 * 60; // 1시간

    /**
     * 임시 파일 업로드를 위한 Presigned URL 생성
     *
     * @param fileType 파일 타입 (image/jpeg, image/png 등)
     * @param fileExtension 파일 확장자 (jpg, png 등)
     * @return Presigned URL 정보 (URL, 키)
     */
    public PresignedUrlDto generatePresignedUrl(String fileType, String fileExtension) {
        // 고유한 파일 이름 생성
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String key = TEMP_FOLDER + fileName;

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(fileType);

        // Presigned URL 생성
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + PRESIGNED_URL_EXPIRATION);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        
        generatePresignedUrlRequest.setContentType(fileType);
        generatePresignedUrlRequest.addRequestParameter("x-amz-acl", "public-read");

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return PresignedUrlDto.builder()
                .url(url.toString())
                .build();
    }

    /**
     * 임시 파일을 지정된 폴더로 이동
     *
     * @param destinationPath 목적 경로
     * @param imageUrl 이미지 URL
     * @return 이동된 파일의 URL
     */
    public String moveFile(String destinationPath, String imageUrl) {
        // URL 에서 키 추출
        String sourceKey = extractKeyFromUrl(imageUrl);
        
        // 파일이 temp 폴더에 있는지 확인
        if (!sourceKey.startsWith(TEMP_FOLDER)) {
            throw new IllegalArgumentException("Source file must be in the temp folder");
        }

        // 파일 이름 추출
        String fileName = sourceKey.substring(TEMP_FOLDER.length());
        
        // 대상 키 생성
        String destinationKey = destinationPath + fileName;
        
        // 파일 복사
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                bucketName, 
                sourceKey, 
                bucketName, 
                destinationKey)
                .withCannedAccessControlList(CannedAccessControlList.PublicRead);
        
        amazonS3.copyObject(copyObjectRequest);
        
        // 원본 파일 삭제
        amazonS3.deleteObject(bucketName, sourceKey);
        
        // 새 파일의 URL 반환
        return amazonS3.getUrl(bucketName, destinationKey).toString();
    }
    
    /**
     * 임시 폴더에 파일을 업로드하고 URL을 반환
     *
     * @param data 파일 데이터 (byte array)
     * @param fileExtension 파일 확장자
     * @return 업로드된 파일의 URL
     */
    public String uploadTempFile(byte[] data, String fileExtension) {
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String key = TEMP_FOLDER + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(getContentType(fileExtension));

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        return amazonS3.getUrl(bucketName, key).toString();
    }

    /**
     * URL 에서 S3 객체 키 추출
     *
     * @param url S3 객체 URL
     * @return S3 객체 키
     */
    private String extractKeyFromUrl(String url) {
        // URL 에서 버킷 이름 이후의 경로 추출
        String bucketEndpoint = bucketName + ".s3.";
        int bucketIndex = url.indexOf(bucketEndpoint);
        
        if (bucketIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format");
        }
        
        // 버킷 이름 이후의 첫 번째 '/' 찾기
        int pathStartIndex = url.indexOf("/", bucketIndex + bucketEndpoint.length());
        
        if (pathStartIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format");
        }
        
        // 키 추출 (첫 번째 '/' 이후의 모든 문자)
        return url.substring(pathStartIndex + 1);
    }

    /**
     * 파일 확장자에 따라 컨텐츠 타입을 반환합니다.
     *
     * @param fileExtension 파일 확장자 (예: "jpg", "png", "pdf")
     * @return 컨텐츠 타입 (예: "image/jpeg", "image/png", "application/pdf")
     */
    private String getContentType(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "mp3" -> "audio/mpeg";
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt", "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream"; // 기본 컨텐츠 타입
        };
    }
}