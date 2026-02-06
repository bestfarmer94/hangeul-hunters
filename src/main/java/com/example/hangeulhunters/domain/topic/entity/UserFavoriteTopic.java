package com.example.hangeulhunters.domain.topic.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 사용자 즐겨찾기 주제 엔티티
 * 사용자가 즐겨찾기한 주제를 관리
 */
@Entity
@Table(name = "user_favorite_topic")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserFavoriteTopic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID
     */
    @Column(nullable = false, name = "user_id")
    private Long userId;

    /**
     * 주제 ID
     */
    @Column(nullable = false, name = "topic_id")
    private Long topicId;
}
