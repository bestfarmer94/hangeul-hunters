package com.example.hangeulhunters.application.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Presigned URL 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Presigned URL 요청 DTO")
public class PresignedUrlRequest {
    
    @NotBlank(message = "파일 타입은 필수입니다")
    @Schema(description = "파일 타입 (MIME 타입)", example = "image/jpeg", required = true)
    private String fileType;
    
    @NotBlank(message = "파일 확장자는 필수입니다")
    @Schema(description = "파일 확장자", example = "jpg", required = true)
    private String fileExtension;
}