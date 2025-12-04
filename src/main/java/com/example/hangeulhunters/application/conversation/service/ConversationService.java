package com.example.hangeulhunters.application.conversation.service;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFilterRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationRequest;
import com.example.hangeulhunters.application.conversation.dto.InterviewRequest;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.domain.conversation.repository.ConversationRepository;
import com.example.hangeulhunters.domain.persona.constant.PersonaVoice;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import com.example.hangeulhunters.domain.persona.repository.AIPersonaRepository;
import com.example.hangeulhunters.domain.topic.entity.ConversationTopic;
import com.example.hangeulhunters.domain.topic.repository.ConversationTopicRepository;
import com.example.hangeulhunters.infrastructure.exception.ForbiddenException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.hangeulhunters.domain.conversation.constant.ConversationType.INTERVIEW;
import static com.example.hangeulhunters.domain.conversation.constant.ConversationType.ROLE_PLAYING;

@Service
@RequiredArgsConstructor
public class ConversationService {

        private final ConversationRepository conversationRepository;
        private final AIPersonaRepository aiPersonaRepository;
        private final ConversationTopicRepository conversationTopicRepository;
        private final UserService userService;
        private final AIPersonaService aIPersonaService;
        private final FileService fileService;

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
                                                        aIPersonaService.getPersonaByIdIncludeDeleted(userId, conversation.getPersonaId()),
                                                        getConversationTopic(conversation.getConversationTopic()).getTrack()
                                                ))
                                                .toList());
        }

        @Transactional(readOnly = true)
        public ConversationDto getConversationById(Long userId, Long conversationId) {
                Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                AIPersonaDto persona = aIPersonaService.getPersonaById(userId, conversation.getPersonaId());

                ConversationTopic conversationTopic = getConversationTopic(conversation.getConversationTopic());

                return ConversationDto.of(conversation, persona, conversationTopic.getTrack(),
                        fileService.getFiles(FileObjectType.CONVERSATION, conversationId));
        }

        /**
         * ConversationTopic 정보 조회
         */
        private ConversationTopic getConversationTopic(String conversationTopic) {
                return conversationTopicRepository.findByNameAndDeletedAtNull(conversationTopic)
                        .orElseThrow(() -> new ResourceNotFoundException("ConversationTopic", "name", conversationTopic));
        }

        @Transactional
        public ConversationDto createRolePlaying(Long userId, ConversationRequest request) {

                // 유저 정보 조회
                UserDto user = userService.getUserById(userId);

                // AI 페르소나 조회
                AIPersonaDto persona = aIPersonaService.getPersonaById(userId, request.getPersonaId());

                // 대화 생성
                Conversation conversation = Conversation.builder()
                        .userId(userId)
                        .personaId(request.getPersonaId())
                        .conversationType(ROLE_PLAYING)
                        .conversationTopic(request.getConversationTopic())
                        .status(ConversationStatus.ACTIVE)
                        .situation(request.getSituation().getSituation())
                        .createdBy(userId)
                        .build();
                Conversation savedConversation = conversationRepository.save(conversation);

                return ConversationDto.of(savedConversation,
                        aIPersonaService.getPersonaById(userId, savedConversation.getPersonaId()),
                        getConversationTopic(request.getConversationTopic()).getTrack());
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
        public void updateLastActivity(Long conversationId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

                conversation.updateLastActivity();
                conversationRepository.save(conversation);
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
                                .age(INTERVIEW.getDefaultAge())
                                .aiRole(INTERVIEW.getAiRole())
                                .userRole(INTERVIEW.getUserRole())
                                .description(String.format(INTERVIEW.getAiDescriptionFormat(), request.getCompanyName(),
                                                request.getJobTitle()))
                                .voice(voice)
                                .createdBy(userId)
                                .build();
                AIPersona savedInterviewer = aiPersonaRepository.save(interviewer);

                // 면접 대화 생성
                Conversation conversation = Conversation.builder()
                                .userId(userId)
                                .personaId(savedInterviewer.getId())
                                .conversationType(INTERVIEW)
                                .status(ConversationStatus.ACTIVE)
                                .situation(INTERVIEW.getSituation())
                                .interviewCompanyName(request.getCompanyName())
                                .interviewJobTitle(request.getJobTitle())
                                .interviewJobPosting(request.getJobPosting())
                                .interviewStyle(request.getInterviewStyle())
                                .createdBy(userId)
                                .build();
                Conversation savedConversation = conversationRepository.save(conversation);

                // 파일 저장
                List<FileDto> fileDtos = fileService.saveFiles(userId, FileObjectType.CONVERSATION,
                                savedConversation.getId(), request.getFiles());

                return ConversationDto.of(
                                savedConversation,
                                AIPersonaDto.fromEntity(savedInterviewer),
                                getConversationTopic(conversation.getConversationTopic()).getTrack(),
                                fileDtos);
        }
}