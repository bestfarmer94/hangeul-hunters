package com.example.hangeulhunters.application.user.service;

import com.example.hangeulhunters.application.auth.dto.SignUpRequest;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.interest.dto.InterestDto;
import com.example.hangeulhunters.application.interest.service.InterestService;
import com.example.hangeulhunters.application.user.dto.ProfileUpdateRequest;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.common.constant.ImageType;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import com.example.hangeulhunters.domain.user.entity.User;
import com.example.hangeulhunters.domain.user.repository.UserRepository;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestService interestService;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        List<String> interests = getInterestsForUser(id);
        return UserDto.fromEntity(user, interests);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        List<String> interests = getInterestsForUser(user.getId());
        return UserDto.fromEntity(user, interests);
    }

    @Transactional
    public UserDto createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ConflictException("Email is already taken");
        }

        // 프로필 이미지 처리
        String profileImageUrl = fileService.saveImageIfNeed(ImageType.USER_PROFILE,
                signUpRequest.getProfileImageUrl());

        // 사용자 생성 및 저장
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .gender(signUpRequest.getGender())
                .birthDate(signUpRequest.getBirthDate())
                .profileImageUrl(profileImageUrl)
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public UserDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 프로필 이미지 처리
        String profileImageUrl = fileService.saveImageIfNeed(ImageType.USER_PROFILE, request.getProfileImageUrl());

        // 기본 프로필 정보 업데이트
        user.updateProfile(
                userId,
                request.getNickname(),
                request.getBirthDate(),
                request.getKoreanLevel(),
                profileImageUrl);

        // 관심사 업데이트
        if (request.getInterests() != null) {
            interestService.updateUserInterests(userId, request.getInterests());
        }

        User savedUser = userRepository.save(user);
        List<String> interests = getInterestsForUser(userId);
        return UserDto.fromEntity(savedUser, interests);
    }

    private List<String> getInterestsForUser(Long userId) {
        return interestService.getUserInterests(userId).stream()
                .map(InterestDto::getName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findByDeviceId(String deviceId) {
        return userRepository.findByDeviceId(deviceId)
                .map(UserDto::fromEntity);
    }

    @Transactional
    public UserDto createGuestUser(String deviceId) {
        // 게스트 사용자 생성
        User guestUser = User.builder()
                .email("guest_" + deviceId + "@temp.com")
                .password(null)
                .nickname("guest_" + UUID.randomUUID().toString().substring(0, 8))
                .gender(Gender.NONE)
                .birthDate(LocalDate.of(1970, 1, 1))
                .role(UserRole.ROLE_GUEST)
                .deviceId(deviceId)
                .build();

        User savedUser = userRepository.save(guestUser);
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public UserDto convertGuestToUser(Long userId, SignUpRequest signUpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 게스트 사용자인지 확인
        if (user.getRole() != UserRole.ROLE_GUEST) {
            throw new ConflictException("Only guest users can be converted to regular users");
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ConflictException("Email is already taken");
        }

        // 프로필 이미지 처리
        String profileImageUrl = fileService.saveImageIfNeed(ImageType.USER_PROFILE,
                signUpRequest.getProfileImageUrl());

        // 게스트 사용자를 정식 사용자로 전환 (기존 엔티티 재사용)
        User convertedUser = User.builder()
                .id(user.getId())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .gender(signUpRequest.getGender())
                .birthDate(signUpRequest.getBirthDate())
                .profileImageUrl(profileImageUrl)
                .role(UserRole.ROLE_USER)
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .koreanLevel(user.getKoreanLevel())
                .deviceId(null) // device_id 제거
                .creditPoint(250) // 신규 가입 시 250 포인트 지급
                .build();

        User savedUser = userRepository.save(convertedUser);
        return UserDto.fromEntity(savedUser);
    }
}