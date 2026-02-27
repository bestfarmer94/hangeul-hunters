package com.example.hangeulhunters.infrastructure.service.noonchi;

import com.example.hangeulhunters.application.language.dto.ScenarioContextRequest;
import com.example.hangeulhunters.infrastructure.config.NoonchiAiProperties;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param request 롤플레잉 시작 요청 DTO
     * @return AI 첫 발화 응답
     */
    public ChatStartResponse startRolePlayingChat(RolePlayingStartRequest request) {

        try {
            ChatStartResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getRolePlayingStart())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatStartResponse.class)
                    .block();

            log.info("Role-playing chat started successfully - conversationId: {}", request.getConversationId());
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to start role-playing chat - conversationId: {}, status: {}, body: {}",
                    request.getConversationId(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to start role-playing chat: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error starting role-playing chat - conversationId: {}", request.getConversationId(),
                    e);
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
     * @return 학습 리포트
     */
    public LearningReportResponse generateLearningReport(Long conversationId) {
        log.info("Generating learning report - conversationId: {}", conversationId);

        try {
            String uri = (properties.getBaseUrl() + properties.getEndpoints().getRolePlayingReport())
                    .replace("{conversationId}", conversationId.toString());

            return webClient.post()
                    .uri(uri)
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(LearningReportResponse.class)
                    .block();

        } catch (WebClientResponseException e) {
            log.error("Failed to generate learning report - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate learning report: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error generating learning report - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error generating learning report", e);
        }
    }

    // ==================== Help API ====================

    /**
     * 롤플레잌 도움 응답 조회 (What should I say?)
     *
     * @param conversationId 대화방 ID
     * @return 도움 응답
     */
    public HelpAiResponse generateRolePlayingHelp(Long conversationId) {
        log.info("Generating role-playing help - conversationId: {}", conversationId);

        try {
            String uri = (properties.getBaseUrl() + properties.getEndpoints().getRolePlayingHelp())
                    .replace("{conversationId}", conversationId.toString());

            HelpAiResponse response = webClient.get()
                    .uri(uri)
                    .header("x-api-key", properties.getApiKey())
                    .retrieve()
                    .bodyToMono(HelpAiResponse.class)
                    .block();

            log.info("Role-playing help generated successfully - conversationId: {}", conversationId);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to generate role-playing help - conversationId: {}, status: {}, body: {}",
                    conversationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate role-playing help: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error generating role-playing help - conversationId: {}", conversationId, e);
            throw new RuntimeException("Unexpected error generating role-playing help", e);
        }
    }

    // ==================== Scenario Context API ====================

    /**
     * 시나리오 컨텍스트 생성
     *
     * @param request 시나리오 컨텍스트 요청
     * @return 시나리오 컨텍스트 응답
     */
    public ScenarioContextAiResponse generateScenarioContext(ScenarioContextRequest request) {
        log.info("Generating scenario context - scenarioId: {}", request.getScenarioId());

        ScenarioContextAiRequest aiRequest = ScenarioContextAiRequest.builder()
                .myRole(request.getMyRole())
                .aiRole(request.getAiRole())
                .detail(request.getDetail())
                .build();

        try {
            String uri = (properties.getBaseUrl() + properties.getEndpoints().getScenarioGenerateContext())
                    .replace("{scenario_id}", request.getScenarioId().toString());

            ScenarioContextAiResponse response = webClient.post()
                    .uri(uri)
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(aiRequest)
                    .retrieve()
                    .bodyToMono(ScenarioContextAiResponse.class)
                    .block();

            log.info("Scenario context generated successfully - scenarioId: {}", request.getScenarioId());
            return response;

        } catch (WebClientResponseException e) {
            log.error("Failed to generate scenario context - scenarioId: {}, status: {}, body: {}",
                    request.getScenarioId(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate scenario context: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error generating scenario context - scenarioId: {}", request.getScenarioId(), e);
            throw new RuntimeException("Unexpected error generating scenario context", e);
        }
    }

    // ==================== Ask API (SSE Stream) ====================

    /**
     * Ask 대화 시작 (SSE 스트림)
     *
     * @param conversationId 대화방 ID
     * @param askTarget      질문 대상
     * @param closeness      친밀도
     * @param situation      상황 설명
     * @return SSE 스트림
     */
    public Flux<String> startAskConversationStream(
            Long conversationId, String askTarget, String closeness, String situation) {
        log.info("Starting Ask conversation stream - conversationId: {}", conversationId);

        AskStartRequest request = AskStartRequest.builder()
                .conversationId(conversationId)
                .recipient(askTarget)
                .closeness(closeness)
                .detail(situation)
                .build();

        try {
            return webClient.post()
                    .uri(properties.getBaseUrl() + properties.getEndpoints().getAskStart())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(chunk -> log.debug("Received SSE chunk: {}", chunk))
                    .doOnComplete(
                            () -> log.info("Ask conversation stream completed - conversationId: {}", conversationId))
                    .doOnError(error -> log.error("Error in Ask conversation stream - conversationId: {}",
                            conversationId, error));

        } catch (Exception e) {
            log.error("Unexpected error starting Ask conversation stream - conversationId: {}", conversationId, e);
            return Flux.error(new RuntimeException("Failed to start Ask conversation stream", e));
        }
    }

    /**
     * Ask 후속 메시지 전송 (SSE 스트림)
     *
     * @param conversationId 대화방 ID
     * @param userMessage    사용자 메시지
     * @return SSE 스트림
     */
    public Flux<String> sendAskChatStream(Long conversationId, String userMessage) {
        log.info("Sending Ask chat message - conversationId: {}", conversationId);

        AskChatRequest request = AskChatRequest.builder()
                .userMessage(userMessage)
                .build();

        try {
            String uri = (properties.getBaseUrl() + properties.getEndpoints().getAskChat())
                    .replace("{conversationId}", conversationId.toString());

            return webClient.post()
                    .uri(uri)
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(chunk -> log.debug("Received ASK chat SSE chunk: {}", chunk))
                    .doOnComplete(() -> log.info("Ask chat stream completed - conversationId: {}", conversationId))
                    .doOnError(
                            error -> log.error("Error in Ask chat stream - conversationId: {}", conversationId, error));

        } catch (Exception e) {
            log.error("Unexpected error sending Ask chat message - conversationId: {}", conversationId, e);
            return Flux.error(new RuntimeException("Failed to send Ask chat message", e));
        }
    }

    /**
     * RolePlaying 메시지 전송 (SSE 스트림)
     *
     * @param conversationId 대화방 ID
     * @param userMessage    사용자 메시지
     * @return SSE 스트림
     */
    public Flux<String> sendRolePlayMessageStream(Long conversationId, String userMessage) {
        log.info("Sending RolePlaying message stream - conversationId: {}", conversationId);

        Map<String, Object> request = new HashMap<>();
        request.put("user_message", userMessage);

        try {
            return webClient.post()
                    .uri(properties.getBaseUrl() + "/roleplay/conversations/" + conversationId + "/messages")
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(chunk -> log.debug("Received RolePlaying SSE chunk: {}", chunk))
                    .doOnComplete(
                            () -> log.info("RolePlaying message stream completed - conversationId: {}", conversationId))
                    .doOnError(error -> log.error("Error in RolePlaying message stream - conversationId: {}",
                            conversationId, error));

        } catch (Exception e) {
            log.error("Unexpected error sending RolePlaying message stream - conversationId: {}", conversationId, e);
            return Flux.error(new RuntimeException("Failed to send RolePlaying message stream", e));
        }
    }
}
