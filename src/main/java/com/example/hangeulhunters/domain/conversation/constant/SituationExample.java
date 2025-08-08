package com.example.hangeulhunters.domain.conversation.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 대화 상황 예시를 정의하는 열거형입니다.
 * MVP 버전 동안만 유지할 예정입니다.
 */
@Getter
@RequiredArgsConstructor
public enum SituationExample {
    @Schema(description = "Apologizing for a mistake at work.")
    BOSS1("Apologizing for a mistake at work.", "도대체가 뭔 짓을 해 놓은거야?"),

    @Schema(description = "Requesting half-day or annual leave.")
    BOSS2("Requesting half-day or annual leave.", "무슨 일로 찾아왔나?"),

    @Schema(description = "Requesting feedback on work.")
    BOSS3("Requesting feedback on work.", "무슨 일로 찾아왔나?"),

    @Schema(description = "Meeting for the first time and greeting.")
    GF_PARENTS1("Meeting for the first time and greeting.", "반갑네. 부모님 되는 사람일세."),

    @Schema(description = "Conversation over dinner.")
    GF_PARENTS2("Conversation over dinner.", "밥 묵자."),

    @Schema(description = "Apologizing for breaking a picture frame.")
    GF_PARENTS3("Apologizing for breaking a picture frame.", "안 다쳤어?"),

    @Schema(description = "Negotiating prices.")
    CLERK1("Negotiate prices.", "4달라"),

    @Schema(description = "Ask about the origin of the product.")
    CLERK2("Ask about the origin of the product.", "필요한 것 있으면, 말씀해 주세요~~~"),

    @Schema(description = "Complaining about incorrect food orders.")
    CLERK3("Complaining about incorrect food orders.", "주문하신 케이팝데몬헌터스 버거 나왔습니다.");

    public final String situation;
    public final String firstMessage;
}
