package com.example.hangeulhunters.infrastructure.service.aws.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Presigned URL DTO
 * S3 업로드를 위한 Presigned URL 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Presigned URL 정보")
public class PresignedUrlDto {
    
    @Schema(description = "업로드 URL", required = true)
    private String url;
}