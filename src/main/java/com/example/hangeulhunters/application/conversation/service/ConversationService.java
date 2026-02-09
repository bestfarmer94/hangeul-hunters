package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.AskRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFilterRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationRequest;
import com.example.hangeulhunters.application.conversation.dto.InterviewRequest;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaRequest;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.topic.service.TopicService;
import com.example.hangeulhunters.domain.common.constant.Closeness;
import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.constant.InterviewStyle;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.repository.ConversationRepository;
import com.example.hangeulhunters.domain.persona.constant.PersonaVoice;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import com.example.hangeulhunters.domain.persona.repository.AIPersonaRepository;
import com.example.hangeulhunters.domain.topic.entity.ConversationTopic;
import com.example.hangeulhunters.domain.topic.entity.ConversationTopicTask;
import com.example.hangeulhunters.domain.topic.repository.ConversationTopicRepository;
import com.example.hangeulhunters.domain.topic.repository.ConversationTopicTaskRepository;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.hangeulhunters.domain.conversation.constant.ConversationType.ASK;
import static com.example.hangeulhunters.domain.conversation.constant.ConversationType.INTERVIEW;
import static com.example.hangeulhunters.domain.conversation.constant.ConversationType.ROLE_PLAYING;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

        private final ConversationRepository conversationRepository;
        private final AIPersonaRepository aiPersonaRepository;
        private final ConversationTopicRepository conversationTopicRepository;
        private final ConversationTopicTaskRepository conversationTopicTaskRepository;
        private final AIPersonaService aIPersonaService;
        private final FileService fileService;
        private final TopicService topicService;

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
                                Optional.ofNullable(filter.getStatus()).map(ConversationStatus::name).orElse(null),
                                filter.getPersonaId(),
                                filter.getSortBy().name(),
                                pageable);

                return PageResponse.of(
                                conversations,
                                conversations.stream()
                                                .map(conversation -> ConversationDto.of(
                                                                conversation,
                                                                aIPersonaService.getPersonaByIdIncludeDeleted(conversation.getPersonaId()),
                                                                topicService.getTopicByName(conversation.getConversationTopic()).getCategory()
                                                        )
                                                )
                                                .toList());
        }

        @Transactional(readOnly = true)
        public ConversationDto getConversationById(Long userId, Long conversationId) {
                Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                AIPersonaDto persona = aIPersonaService.getPersonaById(userId, conversation.getPersonaId());

                String track = topicService.getTopicByName(conversation.getConversationTopic()).getCategory();

                return ConversationDto.of(conversation, persona, track,
                        fileService.getFiles(FileObjectType.CONVERSATION, conversationId));
        }

        @Transactional
        public void endConversation(Long userId, Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                // 사용자 본인의 대화만 종료 가능
                if (!conversation.getUserId().equals(userId)) {
                        throw new ForbiddenException("User does not own this conversation");
                }

                // 대화 종료 처리
                conversation.endConversation();
                conversationRepository.save(conversation);
        }

        /**
         * 대화 삭제
         *
         * @param userId         사용자 ID
         * @param conversationId 삭제할 대화 ID
         */
        @Transactional
        public void deleteConversation(Long userId, Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                // 사용자 본인의 대화만 삭제 가능
                if (!conversation.getUserId().equals(userId)) {
                        throw new ForbiddenException("User does not own this conversation");
                }

                // 대화 삭제 처리
                conversation.delete(userId);
                conversationRepository.save(conversation);
        }

        /**
         * 대화의 마지막 활동 시간을 업데이트합니다.
         *
         * @param conversationId 대화 ID
         */
        @Transactional
        public void updateConversationLastActivity(Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                conversation.updateLastActivity();
                conversationRepository.save(conversation);
        }

        @Transactional
        public ConversationDto createRolePlaying(Long userId, ConversationRequest request) {

                // ConversationTopic 조회
                ConversationTopic conversationTopic = conversationTopicRepository
                                .findById(request.getConversationTopicId())
                                .orElseThrow(() -> new ResourceNotFoundException("ConversationTopic", "id",
                                                request.getConversationTopicId()));

                // 롤플레잉 페르소나 생성
                AIPersonaDto persona = aIPersonaService.createPersona(userId,
                                AIPersonaRequest.builder()
                                                .name(request.getAiRole())
                                                .gender(Gender.NONE)
                                                .description(conversationTopic.getName())
                                                .userRole(request.getUserRole())
                                                .build());

                // 롤플레잉 대화 생성
                Conversation conversation = Conversation.builder()
                                .userId(userId)
                                .personaId(persona.getPersonaId())
                                .conversationType(ROLE_PLAYING)
                                .conversationTopic(conversationTopic.getName())
                                .status(ConversationStatus.ACTIVE)
                                .situation(request.getSituation())
                                .closeness(request.getCloseness())
                                .createdBy(userId)
                                .build();
                Conversation savedConversation = conversationRepository.save(conversation);

                return ConversationDto.of(
                                savedConversation,
                                persona,
                                conversationTopic.getTrack());
        }

        /**
         * 면접 대화 생성 (페르소나 자동 생성)
         *
         * @param userId  사용자 ID
         * @param request 면접 요청 정보
         * @return 생성된 면접 대화 DTO
         */
        @Transactional
        public ConversationDto createInterview(Long userId, InterviewRequest request) {
                // 면접관 페르소나 자동 생성
                String interviewerName = request.getCompanyName() + INTERVIEW.getAiNamePostFix();
                String voice = PersonaVoice.getPersonaVoice(Relationship.INTERVIEW, Gender.NONE);

                AIPersona interviewer = AIPersona.builder()
                                .userId(userId)
                                .name(interviewerName)
                                .gender(Gender.NONE)
                                .aiRole(INTERVIEW.getAiRole())
                                .userRole(INTERVIEW.getUserRole())
                                .description(INTERVIEW.name())
                                .voice(voice)
                                .createdBy(userId)
                                .build();
                AIPersona savedInterviewer = aiPersonaRepository.save(interviewer);

                // 면접 대화 생성
                Conversation conversation = Conversation.builder()
                                .userId(userId)
                                .personaId(savedInterviewer.getId())
                                // 일단 분류를 롤플레잉으로 통일함
                                .conversationType(ROLE_PLAYING)
                                .conversationTopic(INTERVIEW.name())
                                .status(ConversationStatus.ACTIVE)
                                .situation(INTERVIEW.name())
                                .interviewCompanyName(request.getCompanyName())
                                .interviewJobTitle(request.getJobTitle())
                                .interviewJobPosting(request.getJobPosting())
                                .interviewStyle(InterviewStyle.STANDARD)
                                // .taskCurrentLevel(1)
                                // .taskCurrentName(getConversationTopicTaskByTopicName(INTERVIEW.getSituation(),
                                // 1)
                                // .getName())
                                // .taskAllCompleted(false)
                                .closeness(Closeness.FORMAL.getDisplayName())
                                .createdBy(userId)
                                .build();
                Conversation savedConversation = conversationRepository.save(conversation);

                // 파일 저장
                List<FileDto> fileDtos = fileService.saveFiles(userId, FileObjectType.CONVERSATION,
                                savedConversation.getId(), request.getFiles());

                return ConversationDto.of(
                                savedConversation,
                                AIPersonaDto.fromEntity(savedInterviewer),
                                topicService.getTopicByName(conversation.getConversationTopic()).getCategory(),
                                fileDtos);
        }

        /**
         * ASK 타입 대화 생성 (페르소나 없이 생성)
         *
         * @param userId  사용자 ID
         * @param request ASK 요청 정보
         * @return 생성된 ASK 대화 DTO
         */
        @Transactional
        public ConversationDto createAsk(Long userId, AskRequest request) {
                // ASK 타입 대화 생성 (personaId는 임시로 0L 사용)
                Conversation conversation = Conversation.builder()
                                .userId(userId)
                                .personaId(0L) // ASK 타입은 페르소나가 없으므로 0L 사용
                                .conversationType(ASK)
                                .conversationTopic(ASK.name()) // ASK 타입은 고정 토픽
                                .status(ConversationStatus.ACTIVE)
                                .situation(request.getSituation())
                                .closeness(request.getCloseness())
                                .askTarget(request.getAskTarget())
                                .createdBy(userId)
                                .build();
                Conversation savedConversation = conversationRepository.save(conversation);

                return ConversationDto.of(
                                savedConversation,
                                null, // ASK 타입은 페르소나가 없음
                                null); // ASK 타입은 track이 없음
        }

        /**
         * Conversation의 canGetReport 업데이트
         *
         * @param conversationId 대화 ID
         */
        @Transactional
        public void updateCanGetReport(Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                conversation.updateCanGetReport();
                conversationRepository.save(conversation);

                log.info("Updated canGetReport for conversation: {}", conversationId);
        }
}