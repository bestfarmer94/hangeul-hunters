package com.example.hangeulhunters.infrastructure.service.google;

import com.example.hangeulhunters.infrastructure.config.GCPConfig.GCPProperties;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Google Cloud Text-to-Speech API 서비스
 */
@Service
@RequiredArgsConstructor
public class GoogleApiService {

    private final TextToSpeechClient textToSpeechClient;
    private final GCPProperties gcpProperties;

    /**
     * 텍스트를 음성으로 변환 (커스텀)
     *
     * @param text 음성으로 변환할 텍스트
     * @param voiceName 음성 이름
     * @return 음성 데이터 바이트 배열
     */
    public byte[] synthesize(String text, String voiceName) {
        try {
            // 입력 텍스트 설정
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // 음성 설정
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(gcpProperties.getTts().getLanguageCode())
                    .setName(Optional.ofNullable(voiceName)
                            .orElse(gcpProperties.getTts().getDefaultVoiceName()))
                    .build();

            // 오디오 설정
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // TTS 요청 및 응답 처리
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContent = response.getAudioContent();
            
            return audioContent.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to synthesize speech", e);
        }
    }
}