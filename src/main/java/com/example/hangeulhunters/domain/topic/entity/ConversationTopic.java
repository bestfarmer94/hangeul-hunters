package com.example.hangeulhunters.domain.topic.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.conversation.constant.ConversationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 대화 주제 엔티티
 * 각 대화 타입/트랙별로 학습 과제(task)를 관리
 */
@Entity
@Table(name = "conversation_topic")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConversationTopic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType conversationType;

    /**
     * 롤플레잉 트랙 (career, love, belonging, kpop)
     * INTERVIEW 타입인 경우 null
     */
    @Column(nullable = false)
    private String track;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    /**
     * 이 주제에 속한 task 개수
     */
    @Column(nullable = false)
    private Integer taskCount;
}
