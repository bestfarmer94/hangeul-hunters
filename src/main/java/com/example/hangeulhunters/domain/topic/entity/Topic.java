package com.example.hangeulhunters.domain.topic.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 롤플레잉 대화 주제 엔티티
 * 사용자가 선택할 수 있는 대화 주제 목록
 */
@Entity
@Table(name = "topic")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Topic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 주제 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 주제 카테고리
     */
    @Column(nullable = false)
    private String category;

    /**
     * 주제 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 주제를 나타내는 이미지 URL
     */
    @Column
    private String imageUrl;

    /**
     * 표시 순서
     */
    @Column(nullable = false)
    private Integer displayOrder;
}
