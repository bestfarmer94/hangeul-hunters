package com.example.hangeulhunters.domain.conversation.repository;

import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);

    Optional<Message> findFirstByConversationIdAndTypeOrderByCreatedAtDesc(Long conversationId, MessageType type);
}