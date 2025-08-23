package com.example.hangeulhunters.infrastructure.service.naver;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.EvaluateResult;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.config.NaverApiProperties;
import com.example.hangeulhunters.infrastructure.service.naver.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NaverApiProperties naverApiProperties;
    /**
     * 대화형 AI 응답 생성
     * @param persona AI 페르소나
     * @param level 한국어 레벨
     * @param conversation 대화 정보
     * @param conversationMessages 대화 내역 전체
     * @return AI 응답 메시지
     */
    public String generateAiMessage(AIPersonaDto persona, KoreanLevel level, ConversationDto conversation, List<MessageDto> conversationMessages) {
        try {
            String apiPath = conversation.getChatModelId() != null
                    ? naverApiProperties.getClovaStudio().getTuningModelPath().replace("{taskId}", conversation.getChatModelId())
                    : naverApiProperties.getClovaStudio().getCommonModelPath();

            String url = naverApiProperties.getClovaStudio().getBaseUrl() + apiPath;

            ClovaStudioCommonResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", naverApiProperties.getClovaStudio().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ClovaStudioCommonRequest.ofGenerateReply(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            conversation.getSituation(),
                            level,
                            conversationMessages)
                    )
                    .retrieve()
                    .bodyToMono(ClovaStudioCommonResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if(response != null && response.getStatus().getCode().startsWith("2")) {
                return response.getResult().getMessage().getContent();
            }
        } catch (Exception ignore) {
        }
        return "상황을 이해했습니다. 계속 이야기해 볼까요?";
    }

    /**
     * 사용자 메시지 평가
     * @param persona
     * @param situation
     * @param aiMessage
     * @param userMessage
     * @return
     */
    public EvaluateResult evaluateMessage(AIPersonaDto persona, String situation, String aiMessage, String userMessage) {
        try {
            String url = naverApiProperties.getClovaStudio().getBaseUrl() + naverApiProperties.getClovaStudio().getCommonModelPath();

            ClovaStudioCommonResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", naverApiProperties.getClovaStudio().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(EvaluateScoreRequest.of(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            situation,
                            aiMessage,
                            userMessage)
                    )
                    .retrieve()
                    .bodyToMono(ClovaStudioCommonResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if (response != null && response.getStatus().getCode().startsWith("2")) {
                return objectMapper.readValue(response.getResult().getMessage().getContent(), EvaluateResult.class);
            }
        } catch (Exception ignore) {
        }

        return null;
    }

    /**
     * 문장(메시지) 단위 피드백 (평가 기반)
     */
    public MessageFeedbackResponse feedbackMessage(AIPersonaDto persona, String situation, String aiMessage, String userMessage) {
        String url = naverApiProperties.getClovaStudio().getBaseUrl() + naverApiProperties.getClovaStudio().getCommonModelPath();

        ClovaStudioCommonResponse response = webClient.post()
                .uri(url)
                .header("Authorization", naverApiProperties.getClovaStudio().getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(MessageFeedbackRequest.of(
                        persona.getAiRole(),
                        persona.getUserRole(),
                        situation,
                        aiMessage,
                        userMessage)
                )
                .retrieve()
                .bodyToMono(ClovaStudioCommonResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if(response.getStatus().getCode().startsWith("2")) {
            try {
                return objectMapper.readValue(response.getResult().getMessage().getContent(), MessageFeedbackResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "status code: %s, error message: %s",
                    response.getStatus().getCode(),
                    response.getStatus().getMessage()
            ));
        }
    }

    /**
     * 전체 대화 피드백
     */
    public ConversationFeedbackResponse feedbackConversation(AIPersonaDto persona, String situation, List<MessageDto> messages) {
        try {
            String url = naverApiProperties.getClovaStudio().getBaseUrl() + naverApiProperties.getClovaStudio().getCommonModelPath();

            ClovaStudioCommonResponse response = webClient.post()
                .uri(url)
                .header("Authorization", naverApiProperties.getClovaStudio().getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ConversationFeedbackRequestStudio.of(
                    persona.getAiRole(),
                    persona.getUserRole(),
                    situation,
                    messages)
                )
                .retrieve()
                .bodyToMono(ClovaStudioCommonResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

            if(response.getStatus().getCode().startsWith("2")) {
                return objectMapper.readValue(response.getResult().getMessage().getContent(), ConversationFeedbackResponse.class);
            } 
        } catch (Exception ignore) {
        }

        return null;
    }

    /**
     * 다양한 존댓말 표현들 생성
     */
    public HonorificVariationsResponse generateHonorificVariations(String sourceContent) {
        String url = naverApiProperties.getClovaStudio().getBaseUrl() + naverApiProperties.getClovaStudio().getCommonModelPath();

        ClovaStudioCommonResponse response = webClient.post()
                .uri(url)
                .header("Authorization", naverApiProperties.getClovaStudio().getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(HonorificVariationsRequest.of(sourceContent))
                .retrieve()
                .bodyToMono(ClovaStudioCommonResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if(response.getStatus().getCode().startsWith("2")) {
            try {
                return objectMapper.readValue(response.getResult().getMessage().getContent(), HonorificVariationsResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "status code: %s, error message: %s",
                    response.getStatus().getCode(),
                    response.getStatus().getMessage()
            ));
        }
    }

    /**
     * STT (Speech to Text) 변환 - 일반 텍스트 추출용
     * @param audioUrl 음성 파일 URL (Presigned URL)
     * @return STT 결과 텍스트
     */
    public ClovaSpeechSTTResponse convertSpeechToText(String audioUrl) {
        // 1. Presigned URL에서 오디오 데이터를 byte[]로 다운로드
        byte[] audioBytes = webClient.get()
                .uri(audioUrl)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (audioBytes == null) {
            return null;
        }

        // 2. CLOVA Speech API (단문 인식) 호출
        String url = naverApiProperties.getClovaSpeech().getBaseUrl() + naverApiProperties.getClovaSpeech().getInvokePath() + "?lang=Kor";

        return webClient.post()
                .uri(url)
                .header("X-CLOVASPEECH-API-KEY", naverApiProperties.getClovaSpeech().getSecretKey())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromValue(audioBytes))
                .retrieve()
                .bodyToMono(ClovaSpeechSTTResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    /**
     * Papago 번역
     */
    public String translateContent(String sourceLang, String targetLang, String content) {
        String url = naverApiProperties.getPapago().getBaseUrl() + naverApiProperties.getPapago().getTranslationPath();

        PapagoTranslateResponse response = webClient.post()
                .uri(url)
                .header("x-ncp-apigw-api-key-id", naverApiProperties.getPapago().getClientId())
                .header("x-ncp-apigw-api-key", naverApiProperties.getPapago().getClientSecret())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PapagoTranslateRequest.of(sourceLang, targetLang, content))
                .retrieve()
                .bodyToMono(PapagoTranslateResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        return Optional.ofNullable(response)
                .map(PapagoTranslateResponse::getMessage)
                .map(PapagoTranslateResponse.Message::getResult)
                .map(PapagoTranslateResponse.Message.Result::getTranslatedText)
                .orElseThrow(() -> new IllegalArgumentException("Failed to translate message"));
    }
}