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
     * Task 상태
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskStatus {
        private String task;
        private Boolean completed;
    }

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

        private String track;
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

        @JsonProperty("completed_tasks")
        private List<Boolean> completedTasks;
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

        @JsonProperty("resume_url")
        private String resumeUrl;
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

        @JsonProperty("completed_tasks")
        private List<Boolean> completedTasks;
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
        private String content;

        @JsonProperty("reaction_emoji")
        private String reactionEmoji;

        @JsonProperty("reaction_description")
        private String reactionDescription;

        private String recommendation;

        private List<TaskStatus> tasks;
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

        private List<TaskStatus> tasks;

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

        private String track;
    }

    /**
     * 학습 리포트 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningReportResponse {
        @JsonProperty("politeness_score")
        private Integer politenessScore;

        @JsonProperty("naturalness_score")
        private Integer naturalnessScore;

        @JsonProperty("good_point")
        private String goodPoint;

        private List<ImprovementItem> improvements;

        @JsonProperty("overall_evaluation")
        private String overallEvaluation;
    }
}
