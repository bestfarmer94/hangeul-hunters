package com.example.hangeulhunters.infrastructure.service;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.EvaluateResult;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.config.ClovaStudioProperties;
import com.example.hangeulhunters.infrastructure.dto.EvaluateRequest;
import com.example.hangeulhunters.infrastructure.dto.EvaluateResponse;
import com.example.hangeulhunters.infrastructure.dto.GenerateReplyRequest;
import com.example.hangeulhunters.infrastructure.dto.GenerateReplyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClovaStudioService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ClovaStudioProperties properties;

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
                    ? properties.getTuningModelPath().replace("{taskId}", conversation.getChatModelId())
                    : properties.getCommonModelPath();

            String url = properties.getBaseUrl() + apiPath;

            GenerateReplyResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(GenerateReplyRequest.of(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            conversation.getSituation(),
                            level,
                            userMessage)
                    )
                    .retrieve()
                    .bodyToMono(GenerateReplyResponse.class)
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
            String url = properties.getBaseUrl() + properties.getCommonModelPath();

            EvaluateResponse response = webClient.post()
                    .uri(url)
                    .header("Authorization", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(EvaluateRequest.of(
                            persona.getAiRole(),
                            persona.getUserRole(),
                            situation,
                            aiMessage,
                            userMessage)
                    )
                    .retrieve()
                    .bodyToMono(EvaluateResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            if (response != null && response.getStatus().getCode().startsWith("2")) {
                return objectMapper.readValue(response.getResult().getMessage().getContent(), EvaluateResult.class);
            }
        } catch (Exception ignore) {
        }

        return null;
    }
}