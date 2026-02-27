package com.example.hangeulhunters.infrastructure.service.gcs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Presigned URL DTO
 * GCS 업로드를 위한 Signed URL 정보
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
