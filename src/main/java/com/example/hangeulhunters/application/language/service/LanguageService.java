package com.example.hangeulhunters.application.language.service;

import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.infrastructure.service.google.GoogleApiService;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.ClovaSpeechSTTResponse;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.NoonchiAiService;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.HintResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final NaverApiService naverApiService;
    private final GoogleApiService googleApiService;
    private final FileService fileService;
    private final NoonchiAiService noonchiAiService;

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
     * 텍스트를 음성으로 변환하여 S3 임시 URL을 반환
     *
     * @param text  변환할 텍스트
     * @param voice 음성 유형
     * @return S3에 업로드된 음성 파일의 URL
     */
    @Transactional
    public String convertTextToSpeech(String text, String voice) {
        // 1. TTS 서비스를 통해 음성 데이터 생성
        byte[] audioData = googleApiService.synthesize(text, voice);

        // 2. S3 임시 폴더에 음성 파일 업로드
        return fileService.uploadTempFile(audioData, "mp3");
    }

    /**
     * STT (Speech to Text) 변환 - 일반 텍스트 추출용
     *
     * @param audioUrl 음성 파일 URL (Presigned URL)
     * @return STT 결과 텍스트
     */
    @Transactional(readOnly = true)
    public ClovaSpeechSTTResponse convertSpeechToText(String audioUrl) {
        return naverApiService.convertSpeechToText(audioUrl);
    }

    /**
     * 롤플레잉 힌트 생성
     *
     * @param conversationId 대화방 ID
     * @return 힌트 응답
     */
    @Transactional(readOnly = true)
    public HintResponse generateRolePlayingHint(Long conversationId) {
        return noonchiAiService.generateRolePlayingHint(conversationId);
    }
}