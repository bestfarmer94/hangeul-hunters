package com.example.hangeulhunters.infrastructure.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.hangeulhunters.domain.common.constant.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     * 임시 파일을 지정된 프로필 폴더로 이동
     *
     * @param imageType 이미지 타입 (USER_PROFILE, PERSONA_PROFILE)
     * @param imageUrl 이미지 URL
     * @return 이동된 파일의 URL
     */
    public String moveProfileImage(ImageType imageType, String imageUrl) {
        // URL 에서 키 추출
        String sourceKey = extractKeyFromUrl(imageUrl);
        
        // 파일이 temp 폴더에 있는지 확인
        if (!sourceKey.startsWith(TEMP_FOLDER)) {
            throw new IllegalArgumentException("Source file must be in the temp folder");
        }

        // 파일 이름 추출
        String fileName = sourceKey.substring(TEMP_FOLDER.length());
        
        // 대상 키 생성 (이미지 타입에 따라 경로 결정)
        String destinationKey = imageType.getPath() + fileName;
        
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
}