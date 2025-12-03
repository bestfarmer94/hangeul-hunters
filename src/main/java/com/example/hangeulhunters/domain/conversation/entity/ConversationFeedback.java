package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.conversation.converter.ImprovementListConverter;
import com.example.hangeulhunters.domain.conversation.vo.ImprovementItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
    private String overallEvaluation;

    /**
     * 개선점 목록 (JSON 배열: [{point, tip}, ...])
     * 최대 3개
     */
    @Convert(converter = ImprovementListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private List<ImprovementItem> improvementPoints;
}