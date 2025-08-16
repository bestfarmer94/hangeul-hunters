package com.example.hangeulhunters.domain.conversation.repository;

import com.example.hangeulhunters.domain.conversation.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {
    Optional<MessageFeedback> findByMessageId(Long messageId);
}