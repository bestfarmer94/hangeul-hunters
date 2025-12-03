package com.example.hangeulhunters.domain.topic.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 대화 주제별 개별 과제 엔티티
 */
@Entity
@Table(name = "conversation_topic_task")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConversationTopicTask extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long topicId;

    /**
     * Task 레벨/순서 (1, 2, 3, ...)
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * Task 설명 (영어)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;
}
