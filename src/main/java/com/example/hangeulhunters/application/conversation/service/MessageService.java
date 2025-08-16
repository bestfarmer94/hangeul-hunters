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
import com.example.hangeulhunters.domain.conversation.constant.SituationExample;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.repository.MessageRepository;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
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
    private final NaverApiService naverApiService;
    private final ConversationService conversationService;
    private final UserService userService;

    @Transactional
    public MessageDto sendMessage(Long userId, MessageRequest request) {
        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, request.getConversationId());

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(userId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(userId, conversation.getAiPersona().getPersonaId());

        // 마지막 AI 메시지 조회
        Message lastAiMessage = messageRepository.findFirstByConversationIdAndTypeOrderByCreatedAtDesc(
                        conversation.getConversationId(),
                        MessageType.AI
                )
                .orElseThrow(() -> new IllegalArgumentException("No messages found for this conversation"));

        // STT

        // 평가 수행
       EvaluateResult eval = naverApiService.evaluateMessage(
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
                .createdBy(userId)
                .build();
        messageRepository.save(userMessage);
        
        // 대화의 마지막 활동 시간 업데이트
        conversationService.updateLastActivity(conversation.getConversationId());

        return MessageDto.fromEntity(userMessage);
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
    public void createFirstMessage(Long userId, ConversationDto conversation, SituationExample situationExample) {

        // todo 대화 시작 메시지 생성 (AI) [현재는 수준 미달로 인해, 첫문장 따로 준비]
//        String firstMessage = naverApiService.generateAiMessage(
//                conversation.getAiPersona(),
//                KoreanLevel.INTERMEDIATE,
//                conversation,
//                null
//        );

        Message message = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(situationExample.getFirstMessage())
                .createdBy(userId)
                .build();
        messageRepository.save(message);
        
        // 대화의 마지막 활동 시간 업데이트
        conversationService.updateLastActivity(conversation.getConversationId());
    }

    /**
     * 메시지 번역 기능
     * @param messageId 번역할 메시지 ID
     * @return 번역된 메시지 DTO
     */
    @Transactional
    public String translateMessage(Long messageId) {
        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // 메시지 내용 번역
        String translatedContent = naverApiService.translateContent("ko", "en", message.getContent());

        // 번역된 내용 저장
        message.saveTranslatedContent(translatedContent);
        messageRepository.save(message);

        return translatedContent;
    }

    @Transactional
    public MessageDto createAiReply(Long userId, Long conversationId) {
        // 마지막 메시지 조회
        Message lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("No messages found for this conversation"));

        // 마지막 메시지가 사용자 메시지가 아닌 경우 예외 처리
        if(lastMessage.getType() != MessageType.USER) {
            throw new ConflictException("Ai reply can only be created after a user message");
        }

        // 유저 정보 조회
        UserDto user = userService.getUserById(userId);

        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, conversationId);

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(userId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(userId, conversation.getAiPersona().getPersonaId());

        // 대화 내역 전체 조회
        List<MessageDto> conversationMessages = getAllMessagesByConversationId(conversationId);

        // AI 응답 생성
        String aiReply = naverApiService.generateAiMessage(
                persona,
                user.getKoreanLevel(),
                conversation,
                conversationMessages
        );

        // AI 메시지 저장
        Message aiMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(aiReply)
                .createdBy(userId)
                .build();
        messageRepository.save(aiMessage);
        
        // 대화의 마지막 활동 시간 업데이트
        conversationService.updateLastActivity(conversation.getConversationId());
        
        return MessageDto.fromEntity(aiMessage);
    }

    /**
     * 사용자가 작성한 메시지 수를 카운트합니다.
     * @param userId 사용자 ID
     * @return 사용자가 작성한 메시지 수
     */
    @Transactional(readOnly = true)
    public Integer countMyMessages(Long userId) {
        return messageRepository.countByCreatedByAndType(userId, MessageType.USER);
    }

    /**
     * 메시지 ID로 해당 메시지에 대한 존댓말 변형을 생성하고,
     * AI 페르소나의 역할에 맞는 표현을 추출하여 반환합니다.
     *
     * @param currentUserId 현재 사용자 ID
     * @param messageId 메시지 ID
     * @return AI 역할에 맞는 존댓말 표현 응답
     */
    @Transactional(readOnly = true)
    public HonorificVariationsResponse.ExpressionsByFormality generateHonorificVariations(Long currentUserId, Long messageId) {
        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // 대화 조회
        ConversationDto conversation = conversationService.getConversationById(currentUserId, message.getConversationId());

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(currentUserId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(currentUserId, conversation.getAiPersona().getPersonaId());

        // AI 역할 정보 조회
        Relationship aiRole = Relationship.ofAiRole(persona.getAiRole());

        // 존댓말 표형 생성
        HonorificVariationsResponse variations = naverApiService.generateHonorificVariations(
                message.getContent()
        );

        // AI 역할에 따른 적절한 표현 추출
        return switch (aiRole.getIntimacyLevel()) {
            case "low" -> variations.getLowIntimacyExpressions();
            case "high" -> variations.getHighIntimacyExpressions();
            default -> variations.getMediumIntimacyExpressions();
        };
    }
}