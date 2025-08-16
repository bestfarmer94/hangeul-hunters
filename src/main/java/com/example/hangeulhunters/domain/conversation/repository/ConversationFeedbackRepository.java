package com.example.hangeulhunters.domain.conversation.repository;

import com.example.hangeulhunters.domain.conversation.entity.ConversationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationFeedbackRepository extends JpaRepository<ConversationFeedback, Long> {
    Optional<ConversationFeedback> findByConversationId(Long conversationId);
}