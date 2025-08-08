package com.example.hangeulhunters.domain.conversation.repository;

import com.example.hangeulhunters.domain.conversation.constant.ConversationSortBy;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByIdAndUserId(Long conversationId, Long userId);

    /**
     * 사용자 ID로 대화 목록 페이징 조회 (상태 및 AI 페르소나 필터링 가능)
     *
     * @param userId 사용자 ID
     * @param status 대화 상태 (null 이면 필터링 안함)
     * @param personaId AI 페르소나 ID (null 이면 필터링 안함)
     * @param pageable 페이징 정보
     * @return 페이징된 대화 목록
     */
    @Query(value = "SELECT c.* " +
            "FROM " +
            "   conversation c " +
            "   JOIN ai_persona p " +
            "       ON c.persona_id = p.id " +
            "           and p.deleted_at IS NULL " +
            "WHERE " +
            "   c.user_id = :userId " +
            "   AND (:status IS NULL OR c.status = :status) " +
            "   AND (:personaId IS NULL OR c.persona_id = :personaId) " +
            "   AND c.deleted_at IS NULL " +
            "ORDER BY " +
            "   c.status ASC, " +
            "   CASE WHEN :sortBy = 'PERSONA_NAME_ASC' THEN p.name END ASC, " +
            "   c.created_at DESC", nativeQuery = true)
    Page<Conversation> getConversationsByUser(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("personaId") Long personaId,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}