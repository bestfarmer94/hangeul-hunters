package com.example.hangeulhunters.presentation.user;

import com.example.hangeulhunters.application.user.dto.ProfileUpdateRequest;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController extends ControllerSupport {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "로그인한 사용자의 정보를 조회합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto userDto = userService.getUserById(getCurrentUserId());
        return ResponseEntity.ok(userDto);
    }
    
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "사용자 프로필 정보 업데이트",
        description = "사용자의 프로필 정보(닉네임, 생년월일, 한국어 레벨, 프로필 이미지, 관심사)를 업데이트합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        UserDto updatedUser = userService.updateProfile(getCurrentUserId(), request);
        return ResponseEntity.ok(updatedUser);
    }
}