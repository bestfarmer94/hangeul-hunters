package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.EvaluateResult;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageRequest;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.repository.MessageRepository;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.ClovaStudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AIPersonaService aiPersonaService;
    private final ClovaStudioService clovaStudioService;
    private final ConversationService conversationService;
    private final UserService userService;

    @Transactional
    public MessageDto sendMessage(Long userId, MessageRequest request) {
        // 사용자 정보 조회
        UserDto user = userService.getUserById(userId);

        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, request.getConversationId());

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(userId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(conversation.getAiPersona().getPersonaId(), userId);

        // 마지막 AI 메시지 조회
        Message lastAiMessage = messageRepository.findFirstByConversationIdAndTypeOrderByCreatedAtDesc(
                        conversation.getConversationId(),
                        MessageType.AI
                )
                .orElseThrow(() -> new IllegalArgumentException("No messages found for this conversation"));

        // STT

        // 평가 수행
       EvaluateResult eval = clovaStudioService.evaluateMessage(
               persona,
               conversation.getSituation(),
               lastAiMessage.getContent(),
               request.getContent()
       );

        // 사용자 메시지 저장
        Message userMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.USER)
                .content(request.getContent())
                .politenessScore(eval.getPolitenessScore())
                .naturalnessScore(eval.getNaturalnessScore())
                .build();
        messageRepository.save(userMessage);

        // AI 응답 생성
        String aiReply = clovaStudioService.generateAiMessage(
                persona,
                user.getKoreanLevel(),
                conversation,
                request.getContent()
        );

        // AI 메시지 저장
        Message aiMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(aiReply)
                .build();
        messageRepository.save(aiMessage);

        return MessageDto.fromEntity(aiMessage);
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageDto> getMessagesByConversationId(Long userId, Long conversationId, int page, int size) {
        ConversationDto conversation = conversationService.getConversationById(userId, conversationId);

        if (!conversation.getUserId().equals(userId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> messagePage = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);

        return PageResponse.of(messagePage, MessageDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getAllMessagesByConversationId(Long conversationId) {
        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MessageDto getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .map(MessageDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @Transactional(readOnly = true)
    public MessageDto getPrevMessage(Long messageId) {
        Message targetMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        return messageRepository.findFirstByConversationIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                        targetMessage.getConversationId(),
                        targetMessage.getCreatedAt()
                )
                .map(MessageDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    /**
     * 대화 시작 시 첫 번째 메시지를 생성합니다.
     * @param conversation
     */
    @Transactional
    public void createFirstMessage(ConversationDto conversation) {

        // 대화 시작 메시지 생성 (AI)
        String firstMessage = clovaStudioService.generateAiMessage(
                conversation.getAiPersona(),
                KoreanLevel.INTERMEDIATE,
                conversation,
                null
        );

        Message message = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(firstMessage)
                .build();
        messageRepository.save(message);
    }
}