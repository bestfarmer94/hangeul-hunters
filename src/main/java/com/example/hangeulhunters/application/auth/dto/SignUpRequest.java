package com.example.hangeulhunters.application.auth.dto;

import com.example.hangeulhunters.domain.common.constant.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    private String password;
    
    @NotBlank(message = "Nickname is required")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @NotNull(message = "Gender is required")
    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @NotNull(message = "BirthDate is required")
    @Past(message = "Birth date must be in the past")
    @Schema(description = "생년월일", example = "1990-01-01", format = "date")
    private LocalDate birthDate;
}