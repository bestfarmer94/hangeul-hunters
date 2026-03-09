package com.example.hangeulhunters.infrastructure.service.google;

import com.example.hangeulhunters.infrastructure.config.GCPConfig.GCPProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Optional;

/**
 * Google Cloud Text-to-Speech REST API 서비스
 * (gcloud ADC 대신 API Key 기반 HTTP 호출)
 */
@Service
@RequiredArgsConstructor
public class GoogleApiService {

    private final GCPProperties gcpProperties;
    private final WebClient.Builder webClientBuilder;

    /**
     * 텍스트를 음성으로 변환 (Google TTS REST API)
     *
     * @param text      음성으로 변환할 텍스트
     * @param voiceName 음성 이름 (null이면 기본값 사용)
     * @return 음성 데이터 바이트 배열
     */
    public byte[] synthesize(String text, String voiceName) {
        TtsRequest request = buildRequest(text, voiceName);

        GCPProperties.TTS tts = gcpProperties.getTts();
        String url = tts.getBaseUrl() + tts.getSynthesizePath() + "?key=" + tts.getApiKey();

        TtsResponse response = webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TtsResponse.class)
                .block();

        if (response == null || response.getAudioContent() == null) {
            throw new RuntimeException("Failed to synthesize speech: empty response");
        }

        return Base64.getDecoder().decode(response.getAudioContent());
    }

    private TtsRequest buildRequest(String text, String voiceName) {
        String resolvedVoiceName = Optional.ofNullable(voiceName)
                .orElse(gcpProperties.getTts().getDefaultVoiceName());
        String languageCode = gcpProperties.getTts().getLanguageCode();

        TtsRequest req = new TtsRequest();
        req.setInput(new TtsRequest.SynthesisInput(text));
        req.setVoice(new TtsRequest.VoiceSelectionParams(languageCode, resolvedVoiceName));
        req.setAudioConfig(new TtsRequest.AudioConfig("MP3"));
        return req;
    }

    // ── Inner DTO classes ────────────────────────────────────────────────────

    @Data
    static class TtsRequest {
        private SynthesisInput input;
        private VoiceSelectionParams voice;
        private AudioConfig audioConfig;

        @Data
        static class SynthesisInput {
            private final String text;
        }

        @Data
        static class VoiceSelectionParams {
            private final String languageCode;
            private final String name;
        }

        @Data
        static class AudioConfig {
            private final String audioEncoding;
        }
    }

    @Data
    static class TtsResponse {
        @JsonProperty("audioContent")
        private String audioContent;
    }
}