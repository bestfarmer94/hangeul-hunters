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
        @JsonProperty("conversation_id")
        private Integer conversationId;

        @JsonProperty("scenario_title")
        private String scenarioTitle;

        @JsonProperty("total_turns")
        private Integer totalTurns;

        @JsonProperty("overall_assessment")
        private String overallAssessment;

        @JsonProperty("formality_score")
        private Integer formalityScore;

        @JsonProperty("naturalness_score")
        private Integer naturalnessScore;

        @JsonProperty("conversation_summary")
        private String conversationSummary;

        private String strengths;

        private List<ImprovementItem> improvements;
    }

    // ==================== Help DTOs ====================

    /**
     * 도움 응답 조회 (What should I say?)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelpAiResponse {
        private List<String> suggestions;

        private List<String> explanations;

        private List<String> translations;

        @JsonProperty("wrong_index")
        private Integer wrongIndex;
    }

    // ==================== Ask DTOs ====================

    /**
     * Ask 대화 시작 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AskStartRequest {
        @JsonProperty("conversation_id")
        private Long conversationId;

        private String recipient;

        private String closeness;

        private String detail;
    }

    /**
     * Ask 후속 메시지 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AskChatRequest {
        @JsonProperty("user_message")
        private String userMessage;
    }

    /**
     * Ask SSE 스트림 이벤트
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AskStreamEvent {
        private String type; // "session", "approach_tip", "chunk", "done"

        @JsonProperty("conversation_id")
        private Long conversationId;

        private String content;

        private DoneEventData data;
    }

    /**
     * Ask SSE done 이벤트 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoneEventData {
        @JsonProperty("conversation_id")
        private Long conversationId;

        private String coaching;

        @JsonProperty("ai_message")
        private String aiMessage;

        @JsonProperty("ai_message_en")
        private String aiMessageEn;

        @JsonProperty("approach_tip")
        private String approachTip;

        @JsonProperty("cultural_insight")
        private String culturalInsight;
    }

    // ==================== Scenario Context DTOs ====================

    /**
     * 시나리오 컨텍스트 생성 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioContextAiRequest {
        @JsonProperty("my_role")
        private String myRole;

        @JsonProperty("ai_role")
        private String aiRole;

        private String detail;
    }

    /**
     * 시나리오 컨텍스트 생성 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioContextAiResponse {
        @JsonProperty("my_role")
        private String myRole;

        @JsonProperty("ai_role")
        private String aiRole;

        private String detail;
    }

    // ==================== RolePlaying Stream DTOs ====================

    /**
     * RolePlaying SSE 스트림 이벤트
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePlayStreamEvent {
        private String type; // "ai_chunk", "done", "error"

        @JsonProperty("conversation_id")
        private Long conversationId;

        private String content; // for ai_chunk

        private String message; // for error

        private RolePlayDoneEventData data; // for done
    }

    /**
     * RolePlaying done 이벤트 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePlayDoneEventData {
        @JsonProperty("conversation_id")
        private Long conversationId;

        @JsonProperty("ai_message")
        private String aiMessage;

        @JsonProperty("ai_message_en")
        private String aiMessageEn;

        @JsonProperty("ai_hidden_meaning")
        private String aiHiddenMeaning;

        @JsonProperty("visual_action")
        private String visualAction;

        @JsonProperty("user_visual_action")
        private String userVisualAction;

        @JsonProperty("situation_description")
        private String situationDescription;

        private RolePlayFeedbackData feedback;

        @JsonProperty("turn_count")
        private Integer turnCount;

        @JsonProperty("can_get_report")
        private Boolean canGetReport;

        @JsonProperty("is_conversation_ending")
        private Boolean isConversationEnding;
    }

    /**
     * RolePlaying 피드백 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePlayFeedbackData {
        @JsonProperty("feedback_text")
        private String feedbackText;

        @JsonProperty("is_appropriate")
        private Boolean isAppropriate;

        @JsonProperty("suggested_alternatives")
        private List<String> suggestedAlternatives;
    }
}
