package com.example.hangeulhunters.infrastructure.service.noonchi;

import com.example.hangeulhunters.domain.conversation.constant.ConversationTopicExample;
import com.example.hangeulhunters.infrastructure.config.NoonchiAiProperties;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

/**
 * Noonchi AI 서버 API 통신 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoonchiAiService {

    private final WebClient webClient;

    private final NoonchiAiProperties properties;

    // ==================== Role-Playing APIs ====================

    /**
     * 롬플레잉 대화 시작
     * 
     * @param conversationId 대화방 ID
     * @param track          롬플레잉 트랙 (career, love, belonging, kpop)
     * @param topic          시나리오 토픽
     * @return AI 첫 발화 응답
     */
    public ChatStartResponse startRolePlayingChat(Long conversationId, String track, ConversationTopicExample topic) {
        log.info("Starting role-playing chat - conversationId: {}, track: {}, topic: {}",
                conversationId, track, topic);

        RolePlayingStartRequest request = RolePlayingStartRequest.builder()
                .conversationId(conversationId)
                .track(track)
                .topic(topic.name())
                .build();

        try {
            ChatStartResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getRolePlayingStart())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatStartResponse.class)
                    .block();

            log.info("Role-playing chat started successfully - conversationId: {}", conversationId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to start role-playing chat - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to start role-playing chat: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error starting role-playing chat - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error starting role-playing chat", e);
        }
    }

    /**
     * 롬플레잉 대화 진행
     * 
     * @param conversationId 대화방 ID
     * @param userMessage    사용자 메시지
     * @param track          롬플레잉 트랙
     * @param topic          시나리오 토픽
     * @return AI 응답
     */
    public ChatResponse chatRolePlayingMessage(Long conversationId, String userMessage,
            String track, String topic) {
        log.info("Sending role-playing message - conversationId: {}, track: {}, topic: {}",
                conversationId, track, topic);

        RolePlayingChatRequest request = RolePlayingChatRequest.builder()
                .conversationId(conversationId)
                .userMessage(userMessage)
                .track(track)
                .topic(topic)
                .build();

        try {
            ChatResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getRolePlayingChat())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            log.info("Role-playing message sent successfully - conversationId: {}", conversationId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to send role-playing message - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to send role-playing message: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending role-playing message - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error sending role-playing message", e);
        }
    }

    // ==================== Interview APIs ====================

    /**
     * 면접 대화 시작
     * 
     * @param conversationId 대화방 ID
     * @param resumeUrls     이력서 파일 URL 목록 (선택)
     * @return AI 첫 발화 응답
     */
    public ChatStartResponse startInterviewChat(Long conversationId, List<String> resumeUrls) {
        log.info("Starting interview chat - conversationId: {}, resumeCount: {}",
                conversationId, resumeUrls != null ? resumeUrls.size() : 0);

        InterviewStartRequest request = InterviewStartRequest.builder()
                .conversationId(conversationId)
                .resumeUrls(resumeUrls)
                .build();

        try {
            ChatStartResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getInterviewStart())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatStartResponse.class)
                    .block();

            log.info("Interview chat started successfully - conversationId: {}", conversationId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to start interview chat - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to start interview chat: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error starting interview chat - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error starting interview chat", e);
        }
    }

    /**
     * 면접 대화 진행
     * 
     * @param conversationId 대화방 ID
     * @param userMessage    사용자 메시지
     * @return AI 응답
     */
    public ChatResponse chatInterviewMessage(Long conversationId, String userMessage) {
        log.info("Sending interview message - conversationId: {}", conversationId);

        InterviewChatRequest request = InterviewChatRequest.builder()
                .conversationId(conversationId)
                .userMessage(userMessage)
                .build();

        try {
            ChatResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getInterviewChat())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            log.info("Interview message sent successfully - conversationId: {}", conversationId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to send interview message - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to send interview message: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending interview message - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error sending interview message", e);
        }
    }

    // ==================== Translation API ====================

    /**
     * 메시지 번역
     * 
     * @param message    번역할 메시지
     * @param sourceLang 원본 언어 (ko, en, ja, zh-CN)
     * @param targetLang 대상 언어 (ko, en, ja, zh-CN)
     * @return 번역 결과
     */
    public TranslationResponse translateMessage(String message, String sourceLang, String targetLang) {
        log.info("Translating message - from: {} to: {}", sourceLang, targetLang);

        TranslationRequest request = TranslationRequest.builder()
                .message(message)
                .sourceLang(sourceLang)
                .targetLang(targetLang)
                .build();

        try {
            TranslationResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getTranslation())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TranslationResponse.class)
                    .block();

            log.info("Message translated successfully");
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to translate message - status: {}, body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to translate message: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error translating message", e);
            throw new RuntimeException("Unexpected error translating message", e);
        }
    }

    // ==================== Report API ====================

    /**
     * 학습 리포트 생성
     * 
     * @param conversationId 대화방 ID
     * @param track          시나리오 트랙 (career, love, belonging, kpop, interview)
     * @return 학습 리포트
     */
    public LearningReportResponse generateLearningReport(Long conversationId, String track) {
        log.info("Generating learning report - conversationId: {}, track: {}", conversationId, track);

        LearningReportRequest request = LearningReportRequest.builder()
                .conversationId(conversationId)
                .track(track)
                .build();

        try {
            LearningReportResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getReport())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LearningReportResponse.class)
                    .block();

            log.info("Learning report generated successfully - conversationId: {}, politeness: {}, naturalness: {}",
                    conversationId, response.getPolitenessScore(), response.getNaturalnessScore());
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to generate learning report - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate learning report: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error generating learning report - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error generating learning report", e);
        }
    }
}
