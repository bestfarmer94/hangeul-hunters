package com.example.hangeulhunters.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게스트 로그인 요청")
public class GuestLoginRequest {

    @NotBlank(message = "Device ID는 필수입니다")
    @Schema(description = "디바이스 ID (클라이언트에서 생성한 UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String deviceId;
}
