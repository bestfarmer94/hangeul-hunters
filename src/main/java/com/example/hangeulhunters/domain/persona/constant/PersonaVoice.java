package com.example.hangeulhunters.domain.persona.constant;

import com.example.hangeulhunters.domain.common.constant.Gender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 페르소나 음성 타입
 * AI 페르소나의 음성 유형을 정의합니다.
 * 각 음성은 특정 관계와 성별에 따라 선택됩니다.
 * - 사용하는 Voice 만 담았습니다.
 */
@Getter
@RequiredArgsConstructor
public enum PersonaVoice {
    WAVENET_B("ko-KR-Wavenet-B", Gender.FEMALE),
    WAVENET_C("ko-KR-Wavenet-C", Gender.MALE),
    WAVENET_D("ko-KR-Wavenet-D", Gender.FEMALE),
    LEDA("ko-KR-Chirp3-HD-Leda", Gender.FEMALE);

    private final String voiceName;
    private final Gender gender;

    public static String getPersonaVoice(Relationship relationship, Gender gender) {
        PersonaVoice personaVoice = switch (relationship) {
            case BOSS, GF_PARENTS
                    -> gender == Gender.MALE ? WAVENET_C : WAVENET_B;
            case CLERK
                    -> gender == Gender.MALE ? WAVENET_D : LEDA;
            default -> LEDA;
        };

        return personaVoice.getVoiceName();
    }
}
