package com.example.hangeulhunters.domain.conversation.constant;

/**
 * 대화 상태 열거형
 */
public enum ConversationStatus {
    /**
     * 진행 중인 대화
     */
    ACTIVE,
    /**
     * 종료된 대화
     */
    ENDED,
    /**
     * 삭제된 대화
     */
    DELETED
}