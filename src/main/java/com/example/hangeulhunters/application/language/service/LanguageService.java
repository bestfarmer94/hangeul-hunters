package com.example.hangeulhunters.application.language.service;

import com.example.hangeulhunters.application.language.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final NaverApiService naverApiService;

    /**
     * 여러가지 존댓말 표현 생성
     */
    @Transactional(readOnly = true)
    public HonorificVariationsResponse generateHonorificVariations(String aiRole, String sourceContent) {

        // 문장 번역
        String translatedToKoreanContent = naverApiService.translateContent("en", "ko", sourceContent);

        // 존댓말 표현 생성
        return naverApiService.generateHonorificVariations(aiRole, translatedToKoreanContent);
    }
}