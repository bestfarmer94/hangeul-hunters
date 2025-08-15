package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.FeedbackDto;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.domain.conversation.constant.FeedbackTarget;
import com.example.hangeulhunters.domain.conversation.entity.Feedback;
import com.example.hangeulhunters.domain.conversation.repository.FeedbackRepository;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ConversationService conversationService;
    private final AIPersonaService aiPersonaService;
    private final MessageService messageService;
    private final NaverApiService naverApiService;

    /**
     * 문장(메시지) 단위 피드백
     */
    @Transactional(readOnly = true)
    public FeedbackDto feedbackMessage(Long userId, Long messageId) {
        // 메시지 정보 조회
        MessageDto targetMessage = messageService.getMessageById(messageId);

        // 해당 메시지의 피드백이 이미 존재하는지 확인
        Optional<Feedback> existingFeedback = feedbackRepository.findByTargetAndTargetId(FeedbackTarget.MESSAGE, messageId);

        // 이미 피드백이 존재하면 해당 피드백 반환
        if(existingFeedback.isPresent()) {
            return FeedbackDto.fromEntity(existingFeedback.get());
        };

        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, targetMessage.getConversationId());

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(userId, conversation.getAiPersona().getPersonaId());

        // 직전 AI 메시지 조회 (문맥 제공)
        MessageDto prevMessage = messageService.getPrevMessage(messageId);

        // AI 메시지 피드백
        String feedbackContent = naverApiService.feedbackMessage(
                persona,
                conversation.getSituation(),
                prevMessage.getContent(),
                targetMessage.getContent()
        );

        // 피드백 저장
        Feedback feedback = Feedback.builder()
                .target(FeedbackTarget.MESSAGE)
                .targetId(messageId)
                .honorificScore(targetMessage.getPolitenessScore())
                .naturalnessScore(targetMessage.getNaturalnessScore())
                .content(feedbackContent)
                .createdBy(userId)
                .build();
        feedbackRepository.save(feedback);
        return FeedbackDto.fromEntity(feedback);
    }

    /**
     * 대화 전체 피드백
     */
    @Transactional(readOnly = true)
    public void feedbackConversation(Long userId, Long conversationId) {
        // 대화 정보 조회
        ConversationDto conversation = conversationService.getConversationById(userId, conversationId);

        // AI 페르소나 정보 조회
        AIPersonaDto persona = aiPersonaService.getPersonaById(userId, conversation.getAiPersona().getPersonaId());

        // 전체 대화 메시지 조회
        List<MessageDto> messages = messageService.getAllMessagesByConversationId(conversationId);

        // 전체 대화 평가
        String feedbackContent = naverApiService.feedbackConversation(
                persona,
                conversation.getSituation(),
                messages
        );

        // 평균 점수 계산 및 저장
        Integer avgPoliteness = (int) Math.round(messages.stream()
                .map(MessageDto::getPolitenessScore)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0));

        Integer avgNaturalness = (int) Math.round(messages.stream()
                .map(MessageDto::getNaturalnessScore)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0));

        Feedback feedback = Feedback.builder()
                .target(FeedbackTarget.CONVERSATION)
                .targetId(conversationId)
                .honorificScore(avgPoliteness)
                .naturalnessScore(avgNaturalness)
                .content(feedbackContent)
                .createdBy(userId)
                .build();
        feedbackRepository.save(feedback);
    }
}