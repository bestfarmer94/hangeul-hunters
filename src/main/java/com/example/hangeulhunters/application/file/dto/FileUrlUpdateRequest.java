package com.example.hangeulhunters.application.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 URL 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파일 URL 업데이트 요청 DTO")
public class FileUrlUpdateRequest {
    
    @NotBlank(message = "이미지 URL은 필수입니다")
    @Schema(description = "이미지 URL", example = "https://noonchi-bucket.s3.ap-northeast-2.amazonaws.com/temp/123e4567-e89b-12d3-a456-426614174000.jpg", required = true)
    private String imageUrl;
}