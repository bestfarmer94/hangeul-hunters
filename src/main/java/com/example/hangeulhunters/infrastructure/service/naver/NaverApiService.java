package com.example.hangeulhunters.infrastructure.service.naver;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.EvaluateResult;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.config.NaverApiProperties;
import com.example.hangeulhunters.infrastructure.service.naver.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class NaverApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NaverApiProperties.ClovaStudio clovaStudioProperties;
    private final NaverApiProperties.Papago papagoProperties;

    public NaverApiService(WebClient webClient, ObjectMapper objectMapper, NaverApiProperties naverApiProperties) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.clovaStudioProperties = naverApiProperties.getClovaStudio();
        this.papagoProperties = naverApiProperties.getPapago();
    }
    /**
     * 대화형 AI 응답 생성
     * @param persona
     * @param level
     * @param conversation
     * @param userMessage
     * @return
     */
    public String generateAiMessage(AIPersonaDto persona, KoreanLevel level, ConversationDto conversation, String userMessage) {
        try {
            String apiPath = conversation.getChatModelId() != null
                    ? clovaStudioProperties.getTuningModelPath().replace("{taskId}", conversation.getChatModelId())
                    : clovaStudioProperties.getCommonModelPath();

            String url = clovaStudioProperties.getBaseUrl() + apiPath;

            ClovaCommonResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", clovaStudioProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ClovaCommonRequest.ofGenerateReply(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            conversation.getSituation(),
                            level,
                            userMessage)
                    )
                    .retrieve()
                    .bodyToMono(ClovaCommonResponse.class)
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
            String url = clovaStudioProperties.getBaseUrl() + clovaStudioProperties.getCommonModelPath();

            ClovaCommonResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", clovaStudioProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(EvaluateScoreRequest.ofEvaluateScore(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            situation,
                            aiMessage,
                            userMessage)
                    )
                    .retrieve()
                    .bodyToMono(ClovaCommonResponse.class)
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
    public String feedbackMessage(AIPersonaDto persona, String situation, String aiMessage, String userMessage) {
        String url = clovaStudioProperties.getBaseUrl() + clovaStudioProperties.getCommonModelPath();

        ClovaCommonResponse response = webClient.post()
                .uri(url)
                .header("Authorization", clovaStudioProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ClovaCommonRequest.ofFeedbackSentenceRequest(
                        persona.getAiRole(),
                        persona.getUserRole(),
                        situation,
                        aiMessage,
                        userMessage)
                )
                .retrieve()
                .bodyToMono(ClovaCommonResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if(response != null && response.getStatus().getCode().startsWith("2")) {
            return response.getResult().getMessage().getContent();
        }

        return null;
    }

    /**
     * 전체 대화 피드백
     */
    public String feedbackConversation(AIPersonaDto persona, String situation, List<MessageDto> messages) {
        try {
            String url = clovaStudioProperties.getBaseUrl() + clovaStudioProperties.getCommonModelPath();

            ClovaCommonRequest request = ClovaCommonRequest.ofFeedbackConversationRequest(
                    persona.getAiRole(),
                    persona.getUserRole(),
                    situation,
                    messages
            );

            var response = webClient.post()
                    .uri(url)
                    .header("Authorization", clovaStudioProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ClovaCommonResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if(response != null && response.getStatus().getCode().startsWith("2")) {
                return response.getResult().getMessage().getContent();
            }
        } catch (Exception ignore) {
        }

        return null;
    }

    /**
     * Papago 번역
     */
    public String translateMessage(String content) {
        String url = papagoProperties.getBaseUrl() + papagoProperties.getTranslationPath();

        PapagoTranslateResponse response = webClient.post()
                .uri(url)
                .header("x-ncp-apigw-api-key-id", papagoProperties.getClientId())
                .header("x-ncp-apigw-api-key", papagoProperties.getClientSecret())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PapagoTranslateRequest.translateKoToEn(content))
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