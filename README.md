# 눈치 (Noonchi)

Noonchi 서비스는 외국인들이 한국어 학습을 더 효과적이고 재미있게 할 수 있도록 도와주는 대화형 학습 플랫폼입니다.
AI 와의 역할극을 통해 실제 상황에서의 한국어 사용을 연습하고, 즉각적인 피드백을 받을 수 있습니다.

## 주요 기능

### 1. AI 와의 대화 학습
- 다양한 AI 와 역할극 형태의 대화 연습
- 상황별 맞춤형 대화 시나리오 제공
- 실시간 대화 및 응답 생성

### 2. 한국어 피드백 시스템
- 메시지별 상세 피드백
  - 존댓말 적절성 평가
  - 자연스러움 평가
  - 발음 평가
  - 적절한 표현 제안 및 설명
- 대화 전체 피드백
  - 대화 내용 요약
  - 잘한 점 분석
  - 개선할 점 제안
  - 개선을 위한 표현 예시
  - 한국어 능력에 대한 종합 평가

### 3. 편의 기능
- 메시지 번역 기능
- 다양한 존댓말 표현 제안
- 음성 지원
  - Text-to-Speech (TTS): 메시지를 음성으로 변환
  - Speech-to-Text (STT): 음성을 텍스트로 변환

### 사전 요구사항
- JDK 17 이상
- Gradle
- PostgreSQL (개발 환경에서는 H2 인메모리 DB 사용 가능)
- Redis (토큰 관리용)

### 로컬 개발 환경 설정

1. 저장소 클론
```bash
git clone https://github.com/yourusername/hangeul-hunters.git
cd hangeul-hunters
```

2. 환경 변수 설정
application-local.yml 파일 필요

3. 애플리케이션 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

4. API 문서 접근
```
http://localhost:8080/swagger-ui.html
```

## 배포

배포 관련 자세한 내용은 [DEPLOYMENT.md](DEPLOYMENT.md) 문서를 참조하세요.

## API 문서

API 문서는 Swagger UI를 통해 제공됩니다. 서버 실행 후 `/swagger-ui.html` 경로에서 확인할 수 있습니다.