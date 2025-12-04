package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFeedbackDto;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageFeedbackDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.domain.conversation.entity.ConversationFeedback;
import com.example.hangeulhunters.domain.conversation.entity.Message;
import com.example.hangeulhunters.domain.conversation.entity.MessageFeedback;
import com.example.hangeulhunters.domain.conversation.repository.ConversationFeedbackRepository;
import com.example.hangeulhunters.domain.conversation.repository.MessageFeedbackRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.naver.NaverApiService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.ConversationFeedbackResponse;
import com.example.hangeulhunters.infrastructure.service.naver.dto.MessageFeedbackResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

        private final MessageFeedbackRepository messageFeedbackRepository;
        private final ConversationFeedbackRepository conversationFeedbackRepository;

        /**
         * 문장(메시지) 단위 피드백
         */
        @Transactional
        public void saveMessageFeedback(Long userId, Message message, NoonchiAiDto.ChatResponse aiResponse) {

                // 해당 메시지의 피드백이 이미 존재하는지 확인
                Optional<MessageFeedback> existingFeedback = messageFeedbackRepository.findByMessageId(message.getId());

                // 이미 피드백이 존재하면 해당 피드백 반환
                if (existingFeedback.isPresent()) {
                        return;
                }

                // 피드백 저장
                MessageFeedback feedback = MessageFeedback.builder()
                        .messageId(message.getId())
                        .politenessScore(aiResponse.getPolitenessScore())
                        .naturalnessScore(aiResponse.getNaturalnessScore())
                        .pronunciationScore(message.getPronunciationScore())
                        .appropriateExpression(aiResponse.getModelAnswer())
                        .contentsFeedback(aiResponse.getContentsFeedback())
                        .nuanceFeedback(aiResponse.getNuanceFeedback())
                        .createdBy(userId)
                        .build();
                messageFeedbackRepository.save(feedback);
        }

        /**
         * 대화 전체 피드백
         */
        @Transactional
        public ConversationFeedbackDto feedbackConversation(Long userId, Long conversationId) {
//                // 해당 대화의 피드백이 이미 존재하는지 확인
//                Optional<ConversationFeedback> existingFeedback = conversationFeedbackRepository
//                                .findByConversationId(conversationId);
//
//                // 이미 피드백이 존재하면 해당 피드백 반환
//                if (existingFeedback.isPresent()) {
//                        return ConversationFeedbackDto.fromEntity(existingFeedback.get());
//                }
//
//                // 대화 정보 조회
//                ConversationDto conversation = conversationService.getConversationById(userId, conversationId);
//
//                // AI 페르소나 정보 조회
//                AIPersonaDto persona = aiPersonaService.getPersonaById(userId,
//                                conversation.getAiPersona().getPersonaId());
//
//                // 전체 대화 메시지 조회
//                List<MessageDto> messages = messageService.getAllMessagesByConversationId(conversationId);
//
//                // 전체 대화 평가
//                ConversationFeedbackResponse feedbackContent = naverApiService.feedbackConversation(
//                                persona,
//                                conversation.getSituation(),
//                                messages);
//
//                // 평균 점수 계산
//                Integer avgPoliteness = (int) Math.round(messages.stream()
//                                .map(MessageDto::getPolitenessScore)
//                                .filter(java.util.Objects::nonNull)
//                                .mapToInt(Integer::intValue)
//                                .average()
//                                .orElse(0));
//
//                Integer avgNaturalness = (int) Math.round(messages.stream()
//                                .map(MessageDto::getNaturalnessScore)
//                                .filter(java.util.Objects::nonNull)
//                                .mapToInt(Integer::intValue)
//                                .average()
//                                .orElse(0));
//
//                Integer avgPronunciation = (int) Math.round(messages.stream()
//                                .map(MessageDto::getPronunciationScore)
//                                .filter(java.util.Objects::nonNull)
//                                .mapToInt(Integer::intValue)
//                                .average()
//                                .orElse(0));
//
//                // 피드백 저장
//                ConversationFeedback feedback = ConversationFeedback.builder()
//                                .conversationId(conversationId)
//                                .politenessScore(avgPoliteness)
//                                .naturalnessScore(avgNaturalness)
//                                .pronunciationScore(avgPronunciation)
//                                .summary(feedbackContent.getSummary())
//                                .goodPoints(feedbackContent.getGoodPoints())
////                                .improvementPoints(feedbackContent.getImprovementPoints())
//                                .overallEvaluation(feedbackContent.getOverallEvaluation())
//                                .createdBy(userId)
//                                .build();
//                conversationFeedbackRepository.save(feedback);
                return null;
        }

        /**
         * 대화 피드백 조회
         */
        @Transactional(readOnly = true)
        public ConversationFeedbackDto getConversationFeedback(Long conversationId) {
                ConversationFeedback feedback = conversationFeedbackRepository.findByConversationId(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Conversation feedback not found for conversation ID: "
                                                                + conversationId));
                return ConversationFeedbackDto.fromEntity(feedback);
        }
}