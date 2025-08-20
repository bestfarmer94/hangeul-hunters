package com.example.hangeulhunters.application.language.service;

import com.example.hangeulhunters.application.language.dto.TTSRequest;
import com.example.hangeulhunters.infrastructure.service.google.GoogleApiService;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final NaverApiService naverApiService;
    private final GoogleApiService gcpTTSService;

    /**
     * 여러가지 존댓말 표현 생성
     */
    @Transactional(readOnly = true)
    public HonorificVariationsResponse generateHonorificVariations(String sourceContent) {

        // 문장 번역
        String translatedToKoreanContent = naverApiService.translateContent("en", "ko", sourceContent);

        // 존댓말 표현 생성
        return naverApiService.generateHonorificVariations(translatedToKoreanContent);
    }
    
    /**
     * 텍스트를 음성으로 변환
     *
     * @param request TTS 요청 객체
     * @return 음성 데이터 바이트 배열
     */
    @Transactional(readOnly = true)
    public byte[] convertTextToSpeech(TTSRequest request) {
        // 요청에 따라 적절한 TTS 메서드 호출
        return gcpTTSService.synthesize(request.getText(), request.getVoice());
    }
}