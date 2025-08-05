package com.example.hangeulhunters.infrastructure.config;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import com.example.hangeulhunters.domain.persona.repository.AIPersonaRepository;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import com.example.hangeulhunters.domain.user.entity.User;
import com.example.hangeulhunters.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AIPersonaRepository aiPersonaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@example.com")) {
            User adminUser = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .nickname("Admin")
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.now())
                    .koreanLevel(KoreanLevel.NATIVE)
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            
            userRepository.save(adminUser);
        }
        
        // Create test user if not exists
        if (!userRepository.existsByEmail("user@example.com")) {
            User testUser = User.builder()
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user1234"))
                    .nickname("Test User")
                    .gender(Gender.FEMALE)
                    .birthDate(LocalDate.now())
                    .koreanLevel(KoreanLevel.BEGINNER)
                    .role(UserRole.ROLE_USER)
                    .build();
            
            userRepository.save(testUser);
        }
        
        // Create AI personas
        if (aiPersonaRepository.count() == 0) {
            // Teacher persona
            AIPersona teacherPersona = AIPersona.builder()
                    .name("김선생님")
                    .age(45)
                    .gender(Gender.FEMALE)
                    .relationship("teacher")
                    .description("친절하고 열정적인 한국어 선생님입니다. 학생들의 실수를 잘 이해하고 도움을 줍니다.")
                    .build();
            
            // Friend persona
            AIPersona friendPersona = AIPersona.builder()
                    .name("민지")
                    .age(25)
                    .gender(Gender.FEMALE)
                    .relationship("friend")
                    .description("활발하고 친근한 한국인 친구입니다. 일상적인 대화와 한국 문화에 대해 이야기하는 것을 좋아합니다.")
                    .build();
            
            // Elder persona
            AIPersona elderPersona = AIPersona.builder()
                    .name("박할아버지")
                    .age(70)
                    .gender(Gender.MALE)
                    .relationship("elder")
                    .description("지혜롭고 경험이 많은 할아버지입니다. 전통적인 한국 문화와 역사에 대해 많은 이야기를 알고 있습니다.")
                    .build();
            
            // Colleague persona
            AIPersona colleaguePersona = AIPersona.builder()
                    .name("이부장")
                    .age(35)
                    .gender(Gender.MALE)
                    .relationship("colleague")
                    .description("비즈니스 한국어와 직장 문화에 익숙한 회사 동료입니다. 공식적인 상황에서의 대화를 연습하기에 좋습니다.")
                    .build();
            
            aiPersonaRepository.save(teacherPersona);
            aiPersonaRepository.save(friendPersona);
            aiPersonaRepository.save(elderPersona);
            aiPersonaRepository.save(colleaguePersona);
        }
    }
}