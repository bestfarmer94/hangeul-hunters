package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFilterRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationRequest;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.repository.ConversationRepository;
import com.example.hangeulhunters.domain.conversation.repository.MessageRepository;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.ClovaStudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final AIPersonaService aIPersonaService;
    private final ClovaStudioService clovaStudioService;

    /**
     * 사용자의 대화 목록을 필터링하여 페이징 조회
     *
     * @param userId 사용자 ID
     * @param filter 필터링 요청 정보
     * @return 페이징된 대화 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<ConversationDto> getUserConversations(Long userId, ConversationFilterRequest filter) {

        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getSize());
        
        // 필터링된 대화 목록 조회
        Page<Conversation> conversations = conversationRepository.getConversationsByUser(
                userId, 
                filter.getStatus().name(),
                filter.getPersonaId(),
                filter.getSortBy().name(),
                pageable
        );

        return PageResponse.of(
                conversations,
                conversations.stream()
                        .map(conversation -> ConversationDto.of(
                                conversation,
                                aIPersonaService.getPersonaById(conversation.getPersonaId(), userId)
                        ))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public ConversationDto getConversationById(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        return ConversationDto.of(conversation, aIPersonaService.getPersonaById(conversation.getPersonaId(), userId));
    }

    @Transactional
    public ConversationDto createConversation(Long userId, ConversationRequest request) {

        // 유저 정보 조회
        UserDto user = userService.getUserById(userId);

        // AI 페르소나 조회
        AIPersonaDto persona =  aIPersonaService.getPersonaById(request.getPersonaId(), userId);

        // 대화 생성
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .personaId(request.getPersonaId())
                .status(ConversationStatus.ACTIVE)
                .situation(request.getSituation().getSituation())
                .chatModelId(request.getSituation().getChatModelId())
                .build();
        Conversation savedConversation = conversationRepository.save(conversation);

        ConversationDto conversationDto = ConversationDto.of(savedConversation, persona);

        // 대화 시작 메시지 생성 (AI)
        String firstMessage = clovaStudioService.generateAiMessage(
                persona,
                KoreanLevel.INTERMEDIATE,
                conversationDto,
                null
        );

        Message message = Message.builder()
                .conversationId(savedConversation.getId())
                .type(MessageType.AI)
                .content(firstMessage)
                .build();
        messageRepository.save(message);

        return ConversationDto.of(savedConversation, aIPersonaService.getPersonaById(savedConversation.getPersonaId(), userId));
    }

    @Transactional
    public void endConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // 사용자 본인의 대화만 종료 가능
        if (!conversation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not own this conversation");
        }
        
        // 대화 종료 처리
        conversation.endConversation();

        // 피드백 생성

        conversationRepository.save(conversation);
    }
}