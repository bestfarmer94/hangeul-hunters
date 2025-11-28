package com.example.hangeulhunters.application.file.service;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.common.dto.FileRequest;
import com.example.hangeulhunters.application.conversation.dto.InterviewRequest;
import com.example.hangeulhunters.application.file.dto.PresignedUrlRequest;
import com.example.hangeulhunters.domain.common.constant.AudioType;
import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import com.example.hangeulhunters.domain.common.constant.ImageType;
import com.example.hangeulhunters.domain.common.entity.File;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.repository.FileRepository;
import com.example.hangeulhunters.infrastructure.service.aws.S3Service;
import com.example.hangeulhunters.infrastructure.service.aws.dto.PresignedUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 파일 서비스
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Service s3Service;
    private final FileRepository fileRepository;

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
                : imageUrl;
    }

    /**
     * 이미지 URL 저장 (TEMP 에서 영구 저장소로 이동)
     *
     * @param imageType 이미지 타입 (USER_PROFILE, PERSONA_PROFILE)
     * @param imageUrl 이미지 URL
     * @return 업데이트된 프로필 이미지 URL
     */
    public String saveImageUrl(ImageType imageType, String imageUrl) {
        return s3Service.moveFile(imageType.getPath(), imageUrl);
    }

    /**
     * 임시 폴더에 파일을 업로드하고 URL을 반환
     *
     * @param data  데이터 (byte array)
     * @param fileExtension 파일 확장자 (예: mp3, wav 등)
     * @return 업로드된 파일의 URL
     */
    public String uploadTempFile(byte[] data, String fileExtension) {
        return s3Service.uploadTempFile(data, fileExtension);
    }

    /**
     * 오디오 URL 저장 (TEMP 에서 영구 저장소로 이동)
     *
     * @param audioType 오디오 타입
     * @param audioUrl 이미지 URL
     * @return 업데이트된 프로필 이미지 URL
     */
    public String saveAudioUrl(AudioType audioType, String audioUrl) {
        return s3Service.moveFile(audioType.getPath(), audioUrl);
    }

    public List<FileDto> saveFiles(Long userId, FileObjectType objectType, Long objectId, List<FileRequest> fileRequests) {
        // 파일 저장
        List<FileDto> fileDtos = new ArrayList<>();

        if (fileRequests != null && !fileRequests.isEmpty()) {
            for (FileRequest fileRequest : fileRequests) {
                // 파일명 추출 (URL의 마지막 부분)
//                String fileName = fileRequest.getFileUrl().substring(fileRequest.getFileUrl().lastIndexOf('/') + 1);

                File file = File.builder()
                        .objectType(objectType)
                        .objectId(objectId)
                        .fileUrl(fileRequest.getFileUrl())
                        .fileName(fileRequest.getFileName())
                        .fileType(fileRequest.getFileType())
                        .fileSize(fileRequest.getFileSize())
                        .createdBy(userId)
                        .build();
                File savedFile = fileRepository.save(file);
                fileDtos.add(FileDto.fromEntity(savedFile));
            }
        }

        return fileDtos;
    }
}