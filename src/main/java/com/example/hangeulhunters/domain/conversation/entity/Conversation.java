package com.example.hangeulhunters.domain.conversation.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.infrastructure.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "conversation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Conversation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long personaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String situation;

    @Column(nullable = true)
    private String chatModelId;

    @Column(nullable = true)
    private OffsetDateTime endedAt;

    @Builder.Default
    @Column(nullable = false)
    private OffsetDateTime lastActivityAt = DateTimeUtil.now();

    /**
     * 대화 종료
     */
    public void endConversation() {
        this.endedAt = DateTimeUtil.now();
        this.status = ConversationStatus.ENDED;
    }

    /**
     * 마지막 활동 시간 업데이트
     */
    public void updateLastActivity() {
        this.lastActivityAt = DateTimeUtil.now();
    }

    public void delete(Long userId) {
        super.delete(userId);
        this.status = ConversationStatus.DELETED;
    }
}