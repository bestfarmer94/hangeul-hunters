package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFilterRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationRequest;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.repository.ConversationRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
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
    private final AIPersonaService aIPersonaService;

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
    public ConversationDto getConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        return ConversationDto.of(conversation, aIPersonaService.getPersonaById(conversation.getPersonaId(), userId));
    }

    @Transactional
    public ConversationDto createConversation(Long userId, ConversationRequest request) {
        // AI 페르소나 소유 검증
        aIPersonaService.getPersonaById(request.getPersonaId(), userId);

        Conversation conversation = Conversation.builder()
                .userId(userId)
                .personaId(request.getPersonaId())
                .status(ConversationStatus.ACTIVE)
                .situation(request.getSituation())
                .build();
        
        Conversation savedConversation = conversationRepository.save(conversation);
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
        
        conversationRepository.save(conversation);
    }
}