package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.conversation.constant.FeedbackTarget;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "feedback")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackTarget target;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private Integer honorificScore;

    @Column(nullable = false)
    private Integer naturalnessScore;

    @Column(nullable = true)
    private Integer pronunciationScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}