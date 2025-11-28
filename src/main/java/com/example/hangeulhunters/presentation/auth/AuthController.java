package com.example.hangeulhunters.presentation.auth;

import com.example.hangeulhunters.application.auth.dto.*;
import com.example.hangeulhunters.application.auth.service.AuthService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
public class AuthController extends ControllerSupport {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "이메일과 비밀번호로 로그인",
        description = "사용자 계정으로 로그인하고 JWT 토큰을 발급받습니다"
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(
        summary = "새 사용자 등록",
        description = "새 사용자 계정을 생성하고 JWT 토큰을 발급받습니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        AuthResponse response = authService.signup(getCurrentUserId(), signUpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 사용하여 새 액세스 토큰을 발급받습니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃",
        description = "사용자 로그아웃 처리 및 리프레시 토큰을 무효화합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> logout() {
        authService.logout(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guest-login")
    @Operation(
            summary = "게스트 로그인",
            description = "디바이스 ID를 사용하여 게스트로 로그인하고 JWT 토큰을 발급받습니다. 기존 게스트 사용자가 있으면 재사용하고, 없으면 새로 생성합니다."
    )
    public ResponseEntity<AuthResponse> guestLogin(@Valid @RequestBody GuestLoginRequest guestLoginRequest) {
        AuthResponse response = authService.guestLogin(guestLoginRequest);
        return ResponseEntity.ok(response);
    }
}