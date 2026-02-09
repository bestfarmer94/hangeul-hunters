package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFeedbackDto;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageFeedbackDto;
import com.example.hangeulhunters.domain.conversation.entity.ConversationFeedback;
import com.example.hangeulhunters.domain.conversation.entity.MessageFeedback;
import com.example.hangeulhunters.domain.conversation.repository.ConversationFeedbackRepository;
import com.example.hangeulhunters.domain.conversation.repository.MessageFeedbackRepository;
import com.example.hangeulhunters.domain.conversation.vo.ImprovementItem;
import com.example.hangeulhunters.domain.conversation.vo.KeyExpression;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import com.example.hangeulhunters.infrastructure.service.noonchi.NoonchiAiService;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.LearningReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

        private final MessageFeedbackRepository messageFeedbackRepository;
        private final ConversationFeedbackRepository conversationFeedbackRepository;
        private final NoonchiAiService noonchiAiService;

        /**
         * 문장(메시지) 단위 피드백
         */
        @Transactional
        public void saveMessageFeedback(Long userId, Long messageId, NoonchiAiDto.RolePlayFeedbackData feedbackData) {

                // 해당 메시지의 피드백이 이미 존재하는지 확인
                Optional<MessageFeedback> existingFeedback = messageFeedbackRepository.findByMessageId(messageId);

                // 이미 피드백이 존재하면 해당 피드백 반환
                if (existingFeedback.isPresent()) {
                        return;
                }

                // 피드백 저장
                MessageFeedback feedback = MessageFeedback.builder()
                                .messageId(messageId)
                                .politenessScore(0)
                                .naturalnessScore(0)
                                .appropriateExpression(feedbackData.getSuggestedAlternatives().getFirst())
                                .contentsFeedback(feedbackData.getFeedbackText())
                                .nuanceFeedback(feedbackData.getFeedbackText())
                                .createdBy(userId)
                                .build();
                messageFeedbackRepository.save(feedback);
        }

        /**
         * 대화 종료 시 학습 리포트 생성 및 저장
         */
        @Transactional
        public ConversationFeedbackDto saveConversationFeedback(Long userId, ConversationDto conversation,
                        List<MessageDto> messages) {

                // 1. 이미 피드백이 존재하는지 확인
                Optional<ConversationFeedback> existingFeedback = conversationFeedbackRepository
                                .findByConversationId(conversation.getConversationId());

                if (existingFeedback.isPresent()) {
                        log.info("Conversation feedback already exists - conversationId: {}",
                                        conversation.getConversationId());
                        return getConversationFeedback(conversation.getConversationId());
                }

                // 2. 최소 5턴 검증 (canGetReport 확인)
                if (!conversation.getCanGetReport()) {
                        log.error("Cannot generate report - less than 5 turns - conversationId: {}",
                                        conversation.getConversationId());
                        throw new ConflictException("Cannot generate report: minimum 5 turns required");
                }

                // 3. AI 서버 호출 - 학습 리포트 생성
                LearningReportResponse reportResponse = noonchiAiService.generateLearningReport(
                                conversation.getConversationId());

                // 4. ConversationFeedback 저장
                try {
                        // ImprovementItem 변환 (DTO -> Domain)
                        List<ImprovementItem> improvementItems = reportResponse.getImprovements() != null
                                        ? reportResponse.getImprovements().stream()
                                                        .map(item -> ImprovementItem
                                                                        .builder()
                                                                        .point(item.getPoint())
                                                                        .tip(item.getTip())
                                                                        .build())
                                                        .toList()
                                        : List.of();

                        Integer pronunciationScore = messages.stream()
                                        .filter(message -> message.getPronunciationScore() != null)
                                        .mapToInt(MessageDto::getPronunciationScore)
                                        .reduce(0, Integer::sum);

                        ConversationFeedback feedback = ConversationFeedback.builder()
                                        .conversationId(conversation.getConversationId())
                                        .politenessScore(reportResponse.getFormalityScore())
                                        .naturalnessScore(reportResponse.getNaturalnessScore())
                                        .pronunciationScore(pronunciationScore)
                                        .summary(reportResponse.getConversationSummary())
                                        .goodPoints(reportResponse.getStrengths())
                                        .overallEvaluation(reportResponse.getOverallAssessment())
                                        .improvementPoints(improvementItems)
                                        .createdBy(userId)
                                        .build();
                        conversationFeedbackRepository.save(feedback);

                        log.info("Conversation feedback saved - conversationId: {}, feedbackId: {}",
                                        conversation.getConversationId(), feedback.getId());

                        return ConversationFeedbackDto.fromEntity(feedback);

                } catch (Exception e) {
                        throw new RuntimeException("Failed to generate learning report for conversation", e);
                }
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

        @Transactional(readOnly = true)
        public MessageFeedbackDto getMessageFeedback(Long messageId) {
                MessageFeedback feedback = messageFeedbackRepository.findByMessageId(messageId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Message feedback not found for message ID: " + messageId));

                return MessageFeedbackDto.fromEntity(feedback);
        }
}