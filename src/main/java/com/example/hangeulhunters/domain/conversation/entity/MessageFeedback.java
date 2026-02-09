package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 메시지 피드백 엔티티
 */
@Entity
@Table(name = "message_feedback")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MessageFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private Integer politenessScore;

    @Column(nullable = false)
    private Integer naturalnessScore;

    @Column(nullable = true)
    private Integer pronunciationScore;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String appropriateExpression;

    /**
     * 문법/존댓말 피드백 (영어)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentsFeedback;

    /**
     * 맥락/눈치 피드백 (영어)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String nuanceFeedback;
}