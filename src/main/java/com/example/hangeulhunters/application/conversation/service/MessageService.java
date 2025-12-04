package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageRequest;
import com.example.hangeulhunters.application.conversation.dto.MessageSendResponse;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.language.service.LanguageService;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.domain.common.constant.AudioType;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.constant.SituationExample;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.repository.MessageRepository;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.ClovaSpeechSTTResponse;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.NoonchiAiService;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.ChatResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.ChatStartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AIPersonaService aiPersonaService;
    private final NaverApiService naverApiService;
    private final ConversationService conversationService;
    private final UserService userService;
    private final LanguageService languageService;
    private final FileService fileService;
    private final NoonchiAiService noonchiAiService;
    private final FeedbackService feedbackService;

    @Transactional
    public MessageSendResponse sendMessage(Long userId, MessageRequest request) {
        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, request.getConversationId());

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(userId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        String messageContent = request.getContent();
        String audioUrl = request.getAudioUrl();
        Integer pronunciationScore = null;

        // 음성 파일 URL이 있을 경우 STT 수행 (발음 평가)
        if (request.getAudioUrl() != null && !request.getAudioUrl().isBlank()) {

            // 1. STT 수행
            ClovaSpeechSTTResponse sttResult = languageService.convertSpeechToText(request.getAudioUrl());

            // 2. STT 결과 처리 (발음 점수)
            pronunciationScore = sttResult.getAssessment_score();

            // STT 결과가 없거나 비어있으면 예외 처리
            if (sttResult.getText() == null || sttResult.getText().isBlank()) {
                throw new ConflictException("STT resulted in empty text.");
            }
        }

        // 텍스트 메시지 비어있는 경우
        if (messageContent == null || messageContent.isBlank()) {
            throw new IllegalArgumentException("Message content is empty");
        }

        // 1. AI 서버 호출
        ChatResponse aiResponse = processChatWithAi(conversation, messageContent);

        // 2. 사용자 메시지 저장
        Message userMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.USER)
                .content(messageContent)
                .audioUrl(audioUrl)
                .politenessScore(aiResponse.getPolitenessScore())
                .naturalnessScore(aiResponse.getNaturalnessScore())
                .pronunciationScore(pronunciationScore)
                .createdBy(userId)
                .build();
        messageRepository.save(userMessage);

        // 3. AI 메시지 저장
        Message aiMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(aiResponse.getContent())
                .reactionEmoji(aiResponse.getReactionEmoji())
                .reactionDescription(aiResponse.getReactionDescription())
                .reactionReason(aiResponse.getReactionReason())
                .recommendation(aiResponse.getRecommendation())
                .createdBy(userId)
                .build();
        messageRepository.save(aiMessage);

        // 4. 피드백 저장
        feedbackService.saveMessageFeedback(userId, userMessage, aiResponse);

        // 5. 대화 data 처리
        ConversationDto processedConversation = conversationService.processConversation(userId, conversation.getConversationId());

        // USER, AI 메시지 반환
        return MessageSendResponse.of(
                aiResponse.getIsTaskCompleted(),
                processedConversation,
                List.of(MessageDto.fromEntity(userMessage), MessageDto.fromEntity(aiMessage))
        );
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
                targetMessage.getCreatedAt())
                .map(MessageDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    /**
     * 대화 시작 시 첫 번째 메시지를 생성합니다.
     * 
     * @param conversation
     */
    @Transactional
    public void createFirstMessage(Long userId, ConversationDto conversation, SituationExample situationExample) {

        Message message = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.AI)
                .content(situationExample.getFirstMessage())
                .createdBy(userId)
                .build();
        messageRepository.save(message);
    }

    /**
     * 면접 대화 시작 시 첫 번째 메시지를 생성합니다.
     */
    @Transactional
    public void createInterviewFirstMessage(Long userId, ConversationDto conversation) {

        // AI 서버 호출하여 면접 첫 메시지 생성
        try {
            // 1. 대화에 첨부된 이력서 파일 URL 추출
            List<String> resumeUrls = conversation.getFiles()
                    .stream()
                    .map(FileDto::getFileUrl)
                    .toList();

            // 2. AI 서버 호출
            ChatStartResponse aiResponse = noonchiAiService.startInterviewChat(
                    conversation.getConversationId(),
                    resumeUrls);

            // 3. AI 첫 메시지 저장
            Message message = Message.builder()
                    .conversationId(conversation.getConversationId())
                    .type(MessageType.AI)
                    .content(aiResponse.getContent())
                    .reactionEmoji(aiResponse.getReactionEmoji())
                    .reactionDescription(aiResponse.getReactionDescription())
                    .recommendation(aiResponse.getRecommendation())
                    .createdBy(userId)
                    .build();
            messageRepository.save(message);

        } catch (Exception e) {
            // AI 서버 호출 실패 시 기본 메시지 사용
            log.error("Failed to generate AI interview first message for conversation: {}, using fallback",
                    conversation.getConversationId(), e);

            Message fallbackMessage = Message.builder()
                    .conversationId(conversation.getConversationId())
                    .type(MessageType.AI)
                    .content("안녕하세요, 반갑습니다. 먼저 간단하게 자기소개 부탁드립니다.")
                    .createdBy(userId)
                    .build();
            messageRepository.save(fallbackMessage);
        }
    }

    /**
     * 메시지 번역 기능
     * 
     * @param userId    사용자 ID
     * @param messageId 번역할 메시지 ID
     * @return TTS 변환된 오디오 URL
     */
    @Transactional
    public String convertMessageToSpeech(Long userId, Long messageId) {
        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // 이미 오디오 URL이 있는 경우, 해당 URL 반환
        if (message.getAudioUrl() != null) {
            return message.getAudioUrl();
        }

        // 채팅방 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, message.getConversationId());

        // TTS 변환
        String tempAudioUrl = languageService.convertTextToSpeech(message.getContent(),
                conversation.getAiPersona().getVoice());

        // 오디오 URL 저장
        String savedAudioUrl = fileService.saveAudioUrl(AudioType.MESSAGE_AUDIO, tempAudioUrl);

        // 오디오 URL을 메시지에 저장
        message.saveAudioUrl(savedAudioUrl);
        messageRepository.save(message);

        return savedAudioUrl;
    }

    /**
     * 사용자가 작성한 메시지 수를 카운트합니다.
     * 
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
     * @param messageId     메시지 ID
     * @return AI 역할에 맞는 존댓말 표현 응답
     */
    @Transactional(readOnly = true)
    public HonorificVariationsResponse.ExpressionsByFormality generateHonorificVariations(Long currentUserId,
            Long messageId) {
        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // 대화 조회
        ConversationDto conversation = conversationService.getConversationById(currentUserId,
                message.getConversationId());

        // 대화 소유자 확인
        if (!conversation.getUserId().equals(currentUserId)) {
            throw new ForbiddenException("User does not own this conversation");
        }

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(currentUserId,
                conversation.getAiPersona().getPersonaId());

        // AI 역할 정보 조회
        Relationship aiRole = Relationship.ofAiRole(persona.getAiRole());

        // 존댓말 표형 생성
        HonorificVariationsResponse variations = naverApiService.generateHonorificVariations(
                message.getContent());

        // AI 역할에 따른 적절한 표현 추출
        return switch (aiRole.getIntimacyLevel()) {
            case "Close" -> variations.getCloseIntimacyExpressions();
            case "Distant" -> variations.getDistantIntimacyExpressions();
            default -> variations.getMediumIntimacyExpressions();
        };
    }

    /**
     * 메시지 번역 기능
     * 
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

    /**
     * 대화 AI 응답 생성
     */
    private ChatResponse processChatWithAi(ConversationDto conversation, String messageContent) {
        try {
            // AI 응답 생성 및 저장 (ConversationType에 따라 분기)
            return switch (conversation.getConversationType()) {
                case INTERVIEW -> noonchiAiService.chatInterviewMessage(
                        conversation.getConversationId(),
                        messageContent);

                case ROLE_PLAYING -> noonchiAiService.chatRolePlayingMessage(
                        conversation.getConversationId(),
                        messageContent,
                        conversation.getConversationTrack(),
                        conversation.getConversationTopic());

                default -> throw new IllegalArgumentException(
                        "Unsupported conversation type: " + conversation.getConversationType());
            };

        } catch (Exception e) {
            log.error("Failed to generate AI response for conversation: {}",
                    conversation.getConversationId(), e);
            throw new RuntimeException("Failed to generate AI response", e);
        }
    }
}