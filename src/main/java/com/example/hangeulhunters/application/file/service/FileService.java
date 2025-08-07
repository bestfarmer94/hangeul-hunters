package com.example.hangeulhunters.application.file.service;

import com.example.hangeulhunters.application.file.dto.PresignedUrlRequest;
import com.example.hangeulhunters.domain.common.constant.ImageType;
import com.example.hangeulhunters.infrastructure.service.PresignedUrlDto;
import com.example.hangeulhunters.infrastructure.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 파일 서비스
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Service s3Service;

    /**
     * Presigned URL 생성
     *
     * @param request Presigned URL 요청
     * @return Presigned URL 정보
     */
    @Transactional(readOnly = true)
    public PresignedUrlDto generatePresignedUrl(PresignedUrlRequest request) {
        return s3Service.generatePresignedUrl(request.getFileType(), request.getFileExtension());
    }

    @Transactional
    public String saveImageIfNeed(ImageType imageType, String imageUrl) {
        return imageUrl != null && imageUrl.contains("/temp/")
                ? saveImageUrl(imageType, imageUrl)
                : null;
    }

    /**
     * 이미지 URL 저장 (TEMP 에서 영구 저장소로 이동)
     *
     * @param imageUrl 이미지 URL
     * @param imageType 이미지 타입 (USER_PROFILE, PERSONA_PROFILE)
     * @return 업데이트된 프로필 이미지 URL
     */
    public String saveImageUrl(ImageType imageType, String imageUrl) {
        return s3Service.moveProfileImage(imageType, imageUrl);
    }
}