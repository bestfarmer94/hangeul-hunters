package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.*;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.language.service.LanguageService;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.topic.service.TopicService;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.domain.common.constant.AudioType;
import com.example.hangeulhunters.domain.conversation.constant.ConversationTopicExample;
import com.example.hangeulhunters.domain.conversation.constant.ConversationType;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.constant.SituationExample;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.entity.MessageFeedback;
import com.example.hangeulhunters.domain.conversation.repository.ConversationRepository;
import com.example.hangeulhunters.domain.conversation.repository.MessageRepository;
import com.example.hangeulhunters.domain.conversation.repository.MessageFeedbackRepository;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.ClovaSpeechSTTResponse;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.NoonchiAiService;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.ChatResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.ChatStartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;
    private final AIPersonaService aiPersonaService;
    private final NaverApiService naverApiService;
    private final ConversationService conversationService;
    private final LanguageService languageService;
    private final FileService fileService;
    private final NoonchiAiService noonchiAiService;
    private final FeedbackService feedbackService;
    private final TopicService topicService;

    @Transactional
    public MessageDto sendMessage(Long userId, MessageRequest request) {
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

        // 메시지 파싱: *행동묘사* 분리
        String[] parsed = parseMessageContent(messageContent);
        String userVisualAction = parsed[0];
        String actualContent = parsed[1];

        // 사용자 메시지 저장
        Message userMessage = Message.builder()
                .conversationId(conversation.getConversationId())
                .type(MessageType.USER)
                .content(actualContent)
                .visualAction(userVisualAction)
                .audioUrl(audioUrl)
                .pronunciationScore(pronunciationScore)
                .createdBy(userId)
                .build();
        messageRepository.save(userMessage);

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
                targetMessage.getCreatedAt())
                .map(MessageDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @Transactional
    public void createRolePlayingFirstMessage(Long userId, ConversationDto conversation) {

        // AI 서버 호출하여 롤플레잉 첫 메시지 생성
        try {
            // 1. AI 서버 호출
            ChatStartResponse aiResponse = noonchiAiService.startRolePlayingChat(
                    NoonchiAiDto.RolePlayingStartRequest.builder()
                            .conversationId(conversation.getConversationId())
                            .scenarioId(topicService.getTopicByName(conversation.getConversationTopic()).getTopicId())
                            .myRole(conversation.getAiPersona().getUserRole())
                            .aiRole(conversation.getAiPersona().getAiRole())
                            .closeness(conversation.getCloseness())
                            .detail(conversation.getSituation())
                            .build());

            // 2. AI 첫 메시지 저장
            Message aiMessage = Message.builder()
                    .conversationId(conversation.getConversationId())
                    .type(MessageType.AI)
                    .content(aiResponse.getAiMessage())
                    .hiddenMeaning(aiResponse.getAiHiddenMeaning())
                    .visualAction(aiResponse.getVisualAction())
                    .createdBy(userId)
                    .build();
            messageRepository.save(aiMessage);

        } catch (Exception e) {
            // AI 서버 호출 실패 시 기본 메시지 사용
            log.error("Failed to generate AI role-playing first message for conversation: {}, using fallback",
                    conversation.getConversationId(), e);
        }
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
                    .content(aiResponse.getAiMessage())
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

    // /**
    // * 대화 AI 응답 생성
    // */
    // private ChatResponse processChatWithAi(ConversationDto conversation, String
    // messageContent) {
    // try {
    // // AI 응답 생성 및 저장 (ConversationType에 따라 분기)
    // return switch (conversation.getConversationType()) {
    // case INTERVIEW -> noonchiAiService.chatInterviewMessage(
    // conversation.getConversationId(),
    // messageContent);
    //
    // case ROLE_PLAYING -> noonchiAiService.chatRolePlayingMessage(
    // conversation.getConversationId(),
    // messageContent,
    // conversation.getConversationTrack(),
    // Objects.requireNonNull(
    // ConversationTopicExample.getTopicExampleByName(conversation.getConversationTopic()))
    // .name());
    //
    // default -> throw new IllegalArgumentException(
    // "Unsupported conversation type: " + conversation.getConversationType());
    // };
    //
    // } catch (Exception e) {
    // log.error("Failed to generate AI response for conversation: {}",
    // conversation.getConversationId(), e);
    // throw new RuntimeException("Failed to generate AI response", e);
    // }
    // }

    /**
     * Ask 대화 시작 시 첫 번째 메시지를 SSE 스트림으로 생성합니다.
     *
     * @param userId       사용자 ID
     * @param conversation 대화 정보
     * @param askTarget    질문 대상
     * @param closeness    친밀도
     * @param situation    상황 설명
     * @return SSE 스트림
     */
    public Flux<ServerSentEvent<String>> createAskFirstMessageStream(
            Long userId, ConversationDto conversation, String askTarget, String closeness, String situation) {

        log.info("Creating Ask first message stream for conversation: {}", conversation.getConversationId());

        // AI 서버로부터 스트림 받기
        Flux<String> aiStream = noonchiAiService
                .startAskConversationStream(
                        conversation.getConversationId(),
                        askTarget,
                        closeness,
                        situation);

        // DoneEventData를 저장하기 위한 AtomicReference
        final AtomicReference<NoonchiAiDto.DoneEventData> doneDataRef = new AtomicReference<>();

        return aiStream
                .mapNotNull(chunk -> {
                    // 각 청크를 파싱하여 done 이벤트인지 확인
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        NoonchiAiDto.AskStreamEvent event = mapper.readValue(chunk, NoonchiAiDto.AskStreamEvent.class);

                        // done 이벤트인 경우 데이터 저장
                        if ("done".equals(event.getType()) && event.getData() != null) {
                            doneDataRef.set(event.getData());
                            log.info("Received done event with data for conversation: {}",
                                    conversation.getConversationId());
                        }

                        // content가 있으면 반환 (chunk 이벤트)
                        if (event.getContent() != null && !event.getContent().isEmpty()) {
                            return ServerSentEvent.<String>builder()
                                    .data(event.getContent())
                                    .build();
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse chunk as JSON, treating as plain text: {}", e.getMessage());
                        // JSON 파싱 실패 시 그대로 전달
                        return ServerSentEvent.<String>builder()
                                .data(chunk)
                                .build();
                    }

                    // content가 없으면 null 반환 (필터링됨)
                    return null;
                })
                .filter(Objects::nonNull) // null 제거
                .concatWith(Flux.defer(() -> {
                    // 스트림 완료 후 DB 저장 및 done 이벤트 전송
                    log.info("Ask stream completed, saving message to database");

                    try {
                        NoonchiAiDto.DoneEventData doneData = doneDataRef.get();
                        if (doneData != null) {
                            saveDoneEventData(userId, conversation.getConversationId(), doneData);
                        } else {
                            log.warn("No done event data received for conversation: {}",
                                    conversation.getConversationId());
                        }

                        // done 이벤트 전송
                        return Flux.just(ServerSentEvent.<String>builder()
                                .event("done")
                                .data("{\"status\": \"completed\"}")
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to save message after stream completion", e);
                        return Flux.just(ServerSentEvent.<String>builder()
                                .event("error")
                                .data("{\"error\": \"Failed to save message\"}")
                                .build());
                    }
                }))
                .onErrorResume(error -> {
                    // 모든 에러를 스트림 내에서 처리하여 비동기 디스패치 문제 방지
                    log.error("Error in Ask stream for conversation: {}", conversation.getConversationId(), error);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data("{\"error\": \"" + error.getMessage() + "\"}")
                            .build());
                });
    }

    /**
     * 완료된 메시지 데이터를 DB에 저장
     */
    private void saveDoneEventData(Long userId, Long conversationId, NoonchiAiDto.DoneEventData doneData) {
        if (doneData == null) {
            log.warn("AI message is empty for conversation: {}", conversationId);
            return;
        }

        log.info("Saving AI message for conversation: {}", conversationId);

        // AI 메시지 저장
        Message message = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.AI)
                .content(doneData.getAiMessage())
                .translatedContent(doneData.getAiMessageEn())
                .askApproachTip(doneData.getApproachTip())
                .askCulturalInsight(doneData.getCulturalInsight())
                .createdBy(userId)
                .build();
        messageRepository.save(message);

        log.info("Saved AI message for conversation: {}", conversationId);
    }

    // 임시
    @Transactional(readOnly = true)
    public MessageDto getLastMessageInConversation(Long conversationId) {
        return messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
                .map(MessageDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @Transactional
    public void createAskInitialMessages(Long currentUserId, Long conversationId, @Valid AskRequest request) {
        // 일괄 저장용 메시지 리스트
        List<Message> messageList = new ArrayList<>();

        // 1-1. 시스템 메시지1
        Message systemMessage1 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.SYSTEM)
                .content(ConversationType.ASK.getAskSteps().getFirst().getContent())
                .askApproachTip(ConversationType.ASK.getAskSteps().getFirst().getApproachTip())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(systemMessage1);

        // 1-2. 유저 메시지1
        Message userMessage1 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.USER)
                .content(request.getAskTarget())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(userMessage1);

        // 2-1. 시스템 메시지2
        Message systemMessage2 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.SYSTEM)
                .content(ConversationType.ASK.getAskSteps().get(1).getContent())
                .askApproachTip(ConversationType.ASK.getAskSteps().get(1).getApproachTip())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(systemMessage2);

        // 2-2. 유저 메시지2
        Message userMessage2 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.USER)
                .content(request.getCloseness())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(userMessage2);

        // 3-1. 시스템 메시지3
        Message systemMessage3 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.SYSTEM)
                .content(ConversationType.ASK.getAskSteps().getLast().getContent())
                .askApproachTip(ConversationType.ASK.getAskSteps().getLast().getApproachTip())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(systemMessage3);

        // 3-2. 유저 메시지3
        Message userMessage3 = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.SYSTEM)
                .content(request.getSituation())
                .createdBy(currentUserId)
                .createdAt(OffsetDateTime.now())
                .build();
        messageList.add(userMessage3);

        // 4. 일괄 저장
        messageRepository.saveAll(messageList);
    }

    /**
     * RolePlaying 메시지 생성 (스트리밍 수신 후 REST 응답 [임시])
     */
    @Transactional
    public List<MessageDto> processRolePlayWithAi(Long userId, Long conversationId, Long userMessageId,
            String userMessageContent) {
        log.info("Processing RolePlaying AI response - conversationId: {}, userMessageId: {}", conversationId,
                userMessageId);

        // 1. AI 서버로부터 스트림 받기
        Flux<String> aiStream = noonchiAiService.sendRolePlayMessageStream(conversationId, userMessageContent);

        // 2. DoneEventData를 저장하기 위한 AtomicReference
        final AtomicReference<NoonchiAiDto.RolePlayDoneEventData> doneDataRef = new AtomicReference<>();

        // 3. 스트림 처리 (done 이벤트 데이터 추출)
        aiStream
                .mapNotNull(chunk -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        NoonchiAiDto.RolePlayStreamEvent event = mapper.readValue(chunk,
                                NoonchiAiDto.RolePlayStreamEvent.class);

                        // done 이벤트인 경우 데이터 저장
                        if ("done".equals(event.getType()) && event.getData() != null) {
                            doneDataRef.set(event.getData());
                            log.info("Received done event with data for conversation: {}", conversationId);
                        }

                        return event;
                    } catch (Exception e) {
                        log.warn("Failed to parse chunk as JSON: {}", e.getMessage());
                        return null;
                    }
                })
                .blockLast(); // 스트림 완료까지 대기

        // 4. Done 이벤트 데이터로 메시지 저장
        List<MessageDto> messageList = new ArrayList<>();
        NoonchiAiDto.RolePlayDoneEventData doneData = doneDataRef.get();
        if (doneData == null) {
            log.error("No done event data received for conversation: {}", conversationId);
            throw new RuntimeException("Failed to receive AI response");
        }

        // 4-1. System 메시지 저장
        if (doneData.getSituationDescription() != null && !doneData.getSituationDescription().isBlank()) {
            Message systemMessage = Message.builder()
                    .conversationId(conversationId)
                    .type(MessageType.SYSTEM)
                    .content(doneData.getSituationDescription())
                    .situationContext(doneData.getSituationDescription())
                    .createdBy(userId)
                    .build();
            messageRepository.save(systemMessage);

            messageList.add(MessageDto.fromEntity(systemMessage));
        }

        // 4-2. AI 메시지 저장
        Message aiMsg = Message.builder()
                .conversationId(conversationId)
                .type(MessageType.AI)
                .content(doneData.getAiMessage())
                .translatedContent(doneData.getAiMessageEn())
                .hiddenMeaning(doneData.getAiHiddenMeaning())
                .visualAction(doneData.getVisualAction())
                .situationDescription(doneData.getSituationDescription())
                .createdBy(userId)
                .build();
        messageRepository.save(aiMsg);
        messageList.add(MessageDto.fromEntity(aiMsg));

        // 5. 피드백 저장 (있는 경우)
        if (doneData.getFeedback() != null) {
            NoonchiAiDto.RolePlayFeedbackData feedbackData = doneData.getFeedback();

            feedbackService.saveMessageFeedback(userId, userMessageId, feedbackData);

            log.info("Saved RolePlaying feedback for message: {}", userMessageId);
        }

        log.info("Saved RolePlaying messages for conversation: {}", conversationId);

        // 6. Conversation 상태 업데이트 (canGetReport, isConversationEnding)
        // 6-1. canGetReport 업데이트
        if (doneData.getCanGetReport() != null && doneData.getCanGetReport()) {
            conversationService.updateCanGetReport(conversationId);
        }

        // 6-2. isConversationEnding이 true면 대화 종료
        if (Boolean.TRUE.equals(doneData.getIsConversationEnding())) {
            conversationService.endConversation(userId, conversationId);
        }

        // 7. 대화 마지막 활동 시간 업데이트
        conversationService.updateConversationLastActivity(conversationId);

        // 8. 저장된 AI 메시지 반환
        return messageList;
    }

    /**
     * 메시지 내용에서 *행동묘사* 패턴 파싱
     * 맨 앞 또는 맨 뒤에 위치한 *행동* 패턴을 추출
     *
     * @param rawContent 원본 메시지 내용
     * @return [visualAction, actualContent]
     */
    private String[] parseMessageContent(String rawContent) {
        String trimmed = rawContent.trim();
        
        // 패턴 1: 맨 앞에 *행동묘사* 있는 경우
        // 예: "*긴장한 표정으로* 안녕하세요"
        Pattern frontPattern = Pattern.compile("^\\*([^*]+)\\*\\s*(.*)$");
        Matcher frontMatcher = frontPattern.matcher(trimmed);
        
        if (frontMatcher.matches()) {
            String visualAction = frontMatcher.group(1).trim();
            String actualContent = frontMatcher.group(2).trim();
            return new String[]{visualAction, actualContent};
        }
        
        // 패턴 2: 맨 뒤에 *행동묘사* 있는 경우
        // 예: "안녕하세요 *웃으면서*"
        Pattern backPattern = Pattern.compile("^(.*)\\s*\\*([^*]+)\\*$");
        Matcher backMatcher = backPattern.matcher(trimmed);
        
        if (backMatcher.matches()) {
            String actualContent = backMatcher.group(1).trim();
            String visualAction = backMatcher.group(2).trim();
            return new String[]{visualAction, actualContent};
        }

        // 패턴이 없으면 전체를 content로
        return new String[]{null, rawContent};
    }
}
