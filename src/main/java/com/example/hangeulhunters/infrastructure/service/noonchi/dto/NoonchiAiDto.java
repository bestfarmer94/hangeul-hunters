package com.example.hangeulhunters.infrastructure.service.noonchi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Noonchi AI API DTO 컨테이너 클래스
 */
public class NoonchiAiDto {

    // ==================== Common DTOs ====================

    /**
     * 개선점 항목
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImprovementItem {
        private String point;
        private String tip;
    }

    // ==================== Role-Playing DTOs ====================

    /**
     * 롤플레잉 대화 시작 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePlayingStartRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;

        @JsonProperty("scenario_id")
        private Long scenarioId;

        @JsonProperty("my_role")
        private String myRole;

        @JsonProperty("ai_role")
        private String aiRole;

        private String closeness;

        private String detail;
    }

    /**
     * 롤플레잉 대화 진행 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePlayingChatRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;

        @JsonProperty("user_message")
        private String userMessage;

        private String track;

        private String topic;
    }

    // ==================== Interview DTOs ====================

    /**
     * 면접 대화 시작 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewStartRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;

        @JsonProperty("resume_urls")
        private List<String> resumeUrls;
    }

    /**
     * 면접 대화 진행 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewChatRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;

        @JsonProperty("user_message")
        private String userMessage;
    }

    // ==================== Chat Response DTOs ====================

    /**
     * 대화 시작 응답 (AI 첫 발화)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatStartResponse {
        @JsonProperty("ai_message")
        private String aiMessage;

        @JsonProperty("ai_hidden_meaning")
        private String aiHiddenMeaning;

        @JsonProperty("visual_action")
        private String visualAction;

        @JsonProperty("situation_description")
        private String situationDescription;

        @JsonProperty("situation_context")
        private String situationContext;
    }

    /**
     * 대화 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        private String content;

        @JsonProperty("reaction_emoji")
        private String reactionEmoji;

        @JsonProperty("reaction_description")
        private String reactionDescription;

        @JsonProperty("reaction_reason")
        private String reactionReason;

        @JsonProperty("politeness_score")
        private Integer politenessScore;

        @JsonProperty("naturalness_score")
        private Integer naturalnessScore;

        @JsonProperty("contents_feedback")
        private String contentsFeedback;

        @JsonProperty("nuance_feedback")
        private String nuanceFeedback;

        @JsonProperty("model_answer")
        private String modelAnswer;

        private String recommendation;

        @JsonProperty("is_task_completed")
        private Boolean isTaskCompleted;

        @JsonProperty("is_conversation_complete")
        private Boolean isConversationComplete;
    }

    // ==================== Translation DTOs ====================

    /**
     * 번역 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationRequest {
        private String message;

        @JsonProperty("source_lang")
        private String sourceLang;

        @JsonProperty("target_lang")
        private String targetLang;
    }

    /**
     * 번역 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationResponse {
        @JsonProperty("original_message")
        private String originalMessage;

        @JsonProperty("translated_message")
        private String translatedMessage;
    }

    // ==================== Report DTOs ====================

    /**
     * 학습 리포트 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningReportRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;
    }

    /**
     * 학습 리포트 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningReportResponse {
        @JsonProperty("total_turns")
        private Integer totalTurns;

        @JsonProperty("overall_assessment")
        private String overallAssessment;

        @JsonProperty("formality_score")
        private Integer formalityScore;

        @JsonProperty("naturalness_score")
        private Integer naturalnessScore;

        @JsonProperty("cultural_awareness_score")
        private Integer culturalAwarenessScore;

        private String strengths;

        @JsonProperty("areas_to_improve")
        private List<ImprovementItem> areasToImprove;

        @JsonProperty("conversation_summary")
        private String conversationSummary;
    }

    // ==================== Hint DTOs ====================

    /**
     * 힌트 생성 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HintResponse {
        private List<String> hints;

        private List<String> explanations;
    }
}
