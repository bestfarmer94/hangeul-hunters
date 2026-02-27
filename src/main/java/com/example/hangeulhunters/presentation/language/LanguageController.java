package com.example.hangeulhunters.presentation.language;

import com.example.hangeulhunters.application.language.dto.*;
import com.example.hangeulhunters.application.language.service.LanguageService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto.HelpAiResponse;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
@Tag(name = "Language", description = "언어 관련 API")
public class LanguageController extends ControllerSupport {

        private final LanguageService languageService;

        @GetMapping("/honorific-variations")
        @Operation(summary = "여러가지 존댓말 표현 생성", description = "주어진 원문에 대해 다양한 존댓말 표현을 생성합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<HonorificVariationsResponse> honorificVariations(
                        @Parameter(description = "원문") @RequestParam String sourceContent) {
                return ResponseEntity.ok(languageService.generateHonorificVariations(sourceContent));
        }

        @PostMapping("/tts")
        @Operation(summary = "텍스트를 음성으로 변환", description = "입력된 텍스트를 음성 데이터로 변환합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<String> convertTextToSpeech(@Valid @RequestBody TTSRequest request) {
                // TTS 서비스를 통해 음성 변환
                String audioUrl = languageService.convertTextToSpeech(request.getText(), null);

                return ResponseEntity.ok(audioUrl);
        }

        @PostMapping("/stt")
        @Operation(summary = "음성을 텍스트로 변환 (STT)", description = "Presigned URL을 통해 음성 파일을 받아 텍스트로 변환합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<String> convertSpeechToText(@Valid @RequestBody SpeechToTextRequest request) {
                String sttResult = languageService.convertSpeechToText(request.getAudioUrl()).getText();
                return ResponseEntity.ok(sttResult);
        }

        @GetMapping("/help")
        @Operation(summary = "롤플레잉 도움 응답 조회 (What should I say?)", description = "현재 상황에 적절한 한국어 응답 추천 3개를 생성합니다. suggestions, explanations, translations, wrong_index 포함.", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<HelpResponse> generateRolePlayingHelp(
                        @Parameter(description = "대화방 ID") @RequestParam Long conversationId) {
                HelpAiResponse helpAiResponse = languageService.generateRolePlayingHelp(conversationId);
                return ResponseEntity.ok(HelpResponse.of(helpAiResponse));
        }

        @PostMapping("/scenario-context")
        @Operation(summary = "시나리오 컨텍스트 생성", description = "시나리오 ID를 받아 AI 서버를 호출하여 시나리오 컨텍스트를 생성합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<ScenarioContextResponse> generateScenarioContext(
                        @Valid @RequestBody ScenarioContextRequest request) {
                ScenarioContextResponse context = languageService.generateScenarioContext(request);
                return ResponseEntity.ok(context);
        }
}