package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 대화 전체 피드백 엔티티
 */
@Entity
@Table(name = "conversation_feedback")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConversationFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long conversationId;

    @Column(nullable = false)
    private Integer politenessScore;

    @Column(nullable = false)
    private Integer naturalnessScore;
    
    @Column(nullable = true)
    private Integer pronunciationScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String goodPoints;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String improvementPoints;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String improvementExamples;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String overallEvaluation;
}