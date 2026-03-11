package com.example.hangeulhunters.domain.conversation.repository;

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
         * @param userId    사용자 ID
         * @param status    대화 상태 (null 이면 필터링 안함)
         * @param personaId AI 페르소나 ID (null 이면 필터링 안함)
         * @param pageable  페이징 정보
         * @return 페이징된 대화 목록
         */
        @Query(value = "SELECT c.* " +
                        "FROM " +
                        "   conversation c " +
                        "   JOIN ai_persona p " +
                        "       ON c.persona_id = p.id " +
                        "WHERE " +
                        "   c.user_id = :userId " +
                        "   AND (:status IS NULL OR c.status = :status) " +
                        "   AND (:personaId IS NULL OR c.persona_id = :personaId) " +
                        "   AND (:type IS NULL OR c.conversation_type = :type) " +
                        "   AND c.deleted_at IS NULL " +
                        "ORDER BY " +
                        "   CASE WHEN :sortBy = 'PERSONA_NAME_ASC' THEN p.name END ASC, " +
                        "   c.last_activity_at DESC", nativeQuery = true)
        Page<Conversation> getConversationsByUser(
                        @Param("userId") Long userId,
                        @Param("status") String status,
                        @Param("personaId") Long personaId,
                        @Param("type") String type,
                        @Param("sortBy") String sortBy,
                        Pageable pageable);

        /**
         * 키워드로 대화 목록 검색 (conversation_topic, ai_role, user_role, message content 대상)
         *
         * @param userId   사용자 ID
         * @param keyword  검색 키워드
         * @param pageable 페이징 정보
         * @return 페이징된 대화 목록
         */
        @Query(value = "SELECT DISTINCT c.* " +
                        "FROM conversation c " +
                        "       JOIN ai_persona p " +
                        "               ON c.persona_id = p.id " +
                        "       LEFT JOIN message m " +
                        "               ON m.conversation_id = c.id " +
                        "                       AND m.deleted_at IS NULL " +
                        "WHERE c.user_id = :userId " +
                        "   AND c.deleted_at IS NULL " +
                        "   AND (" +
                        "       LOWER(c.conversation_topic) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "       OR LOWER(p.ai_role) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "       OR LOWER(p.user_role) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "       OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "   ) " +
                        "ORDER BY c.last_activity_at DESC", nativeQuery = true)
        Page<Conversation> searchConversationsByKeyword(
                        @Param("userId") Long userId,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}