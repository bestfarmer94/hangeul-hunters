package com.example.hangeulhunters.presentation.language;

import com.example.hangeulhunters.application.language.service.LanguageService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
public class LanguageController extends ControllerSupport {

    private final LanguageService languageService;

    @GetMapping("/honorific-variations")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "여러가지 존댓말 표현 생성",
            description = "주어진 원문에 대해 다양한 존댓말 표현을 생성합니다"
    )
    public ResponseEntity<HonorificVariationsResponse> honorificVariations(
            @Parameter(description = "원문") @RequestParam String sourceContent) {
        return ResponseEntity.ok(languageService.generateHonorificVariations(sourceContent));
    }
}