package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String translatedContent;

    @Column(nullable = true)
    private String audioUrl;

    /**
     * 존댓말 어조 적절성
     */
    @Column(nullable = true)
    private Integer politenessScore;

    /**
     * 문맥 적합도 / 자연스러움
     */
    @Column(nullable = true)
    private Integer naturalnessScore;

    /**
     * 발음 정확도
     */
    @Column(nullable = true)
    private Integer pronunciationScore;

    // AI Response fields (from ChatResponse)
    /**
     * AI 반응 이모지
     */
    @Column(nullable = true)
    private String reactionEmoji;

    /**
     * AI 반응 설명
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String reactionDescription;

    /**
     * AI 반응 이유 (영어)
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String reactionReason;

    /**
     * 추천 응답 (한국어)
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String recommendation;

    /**
     * 번역문 저장
     */
    public void saveTranslatedContent(String translatedContent) {
        this.translatedContent = translatedContent;
    }

    /**
     * audio url 저장
     */
    public void saveAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    /**
     * 피드백 점수 저장
     */
    public void saveFeedbackScores(Integer politenessScore, Integer naturalnessScore) {
        this.politenessScore = politenessScore;
        this.naturalnessScore = naturalnessScore;
    }
}