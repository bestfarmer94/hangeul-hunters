package com.example.hangeulhunters.domain.persona.entity;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AI 페르소나 엔티티
 * 사용자와 대화할 AI 캐릭터의 정보를 담고 있습니다.
 * 각 페르소나는 특정 사용자에게 속하며, 해당 사용자만 접근 가능합니다.
 */
@Entity
@Table(name = "ai_persona")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AIPersona extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String aiRole;

    @Column(nullable = true)
    private String userRole;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private String profileImageUrl;
}