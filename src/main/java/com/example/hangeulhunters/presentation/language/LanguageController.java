package com.example.hangeulhunters.presentation.language;

import com.example.hangeulhunters.application.language.dto.TTSRequest;
import com.example.hangeulhunters.application.language.service.LanguageService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
@Tag(name = "Language", description = "언어 관련 API")
public class LanguageController extends ControllerSupport {

    private final LanguageService languageService;

    @GetMapping("/honorific-variations")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "여러가지 존댓말 표현 생성",
            description = "주어진 원문에 대해 다양한 존댓말 표현을 생성합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<HonorificVariationsResponse> honorificVariations(
            @Parameter(description = "원문") @RequestParam String sourceContent) {
        return ResponseEntity.ok(languageService.generateHonorificVariations(sourceContent));
    }
    
    @PostMapping("/tts")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "텍스트를 음성으로 변환",
            description = "입력된 텍스트를 음성 데이터로 변환합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<byte[]> convertTextToSpeech(@Valid @RequestBody TTSRequest request) {
        // TTS 서비스를 통해 음성 변환
        byte[] audioData = languageService.convertTextToSpeech(request);
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(audioData.length);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=speech.mp3");
        
        return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
    }
}