package com.example.hangeulhunters.domain.conversation.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 대화 상황 예시를 정의하는 열거형입니다.
 * MVP 버전 동안만 유지할 예정입니다.
 */
@Getter
@RequiredArgsConstructor
public enum SituationExample {
    @Schema(description = "Apologizing for a mistake at work.")
    BOSS1("Apologizing to boss for a mistake at work.",
            List.of("어떤 상황에서 발생한 일인지 설명해 주세요.", "어떤 실수를 했는지 구체적으로 말씀해 주세요."),
            "k6n1rvnn"),

    @Schema(description = "Requesting half-day or annual leave.")
    BOSS2("Requesting to boss half-day or annual leave.",
            List.of("휴가 계획에 대해 알려 주세요.", "휴가 사용 날짜와 사유를 알려 주세요."),
            "lmhaeztm"),

    @Schema(description = "Requesting feedback on work.")
    BOSS3("Requesting to boss feedback on work.",
            List.of("이번 프로젝트 중 어려웠던 점은 무엇인가요?", "이번 주 회의 준비는 어떻게 되고 있나요?"),
            "n2bj23zl"),

    @Schema(description = "Meeting for the first time and greeting.")
    GF_PARENTS1("Meeting for the first time and greeting with girlfriend's parents.",
            List.of("드디어 만나네요. 우리 딸에게 얘기 많이 들었어요.", "반가워요. 멀리서 온다고 고생 많았지요?", "기다리고 있었어요. 들어오세요."),
            "oi5rp85k"),

    @Schema(description = "Conversation over dinner.")
    GF_PARENTS2("Conversation over dinner with girlfriend's parents.",
            List.of("입맛에 맞을지 모르겠어요. 많이 드세요.", "식사는 입에 맞는지 걱정이에요. 모자란 게 있으면 말해주세요.", "오랜만에 요리하는 거라서 긴장돼네요. 그래도 정성껏 만들었으니 맛있게 먹어주면 좋겠어요."),
            "py00ccbj"),

    @Schema(description = "Apologizing for breaking a picture frame.")
    GF_PARENTS3("Apologizing for breaking a picture frame to girlfriend's parents.",
            List.of("괜찮아요, 괜찮아요. 중요한 건 사람이지, 액자는 얼마든지 새로 살 수 있으니까요.", "안 다쳐서 다행이에요.", "일부러 그런 것도 아닌데 뭐 어때요. 괜찮아요."),
            "rdu8zghi"),

    @Schema(description = "Negotiating prices.")
    CLERK1("Negotiate prices with clerk.",
            List.of("어떤 음료를 드시겠습니까?", "어떤 커피를 드릴까요? 저희 카페에서는 다양한 종류의 커피와 음료를 제공하고 있습니다.", "커피 외에도 다양한 음료와 디저트를 제공하고 있습니다. 메뉴판을 보시고 마음에 드는 것을 골라보세요."),
            "stohmknh"),

    @Schema(description = "Ask about the origin of the product.")
    CLERK2("Ask about the origin of the product to clerk.",
            List.of("혹시 오늘의 커피 드셔보셨어요? 이번에 새로 들여온 원두인데 반응이 아주 좋아요.", "저희 가게에서는 매일 신선한 원두를 사용하여 커피를 내리고 있습니다. 원하시는 맛과 향에 따라 다양한 원두 중에서 선택하실 수 있습니다."),
            "u9iq9otg"),

    @Schema(description = "Complaining about incorrect food orders.")
    CLERK3("Complaining about incorrect food orders to clerk.",
            List.of("불편하게 해드려 죄송합니다. 어떤 문제로 찾아오셨나요?", "무엇을 도와드릴까요?", "혹시 어떤 점 때문에 그러실까요?"),
            "vpcywszf"),

    @Schema(description = "Interview")
    INTERVIEW("Complaining about incorrect food orders to clerk.",
           List.of("불편하게 해드려 죄송합니다. 어떤 문제로 찾아오셨나요?", "무엇을 도와드릴까요?", "혹시 어떤 점 때문에 그러실까요?"),
            "vpcywszf");

    private final String situation;
    private final List<String> firstMessages;
    private final String chatModelId;

    /**
     * 현재 enum의 firstMessages 에서 랜덤으로 하나를 선택합니다.
     *
     * @return 랜덤하게 선택된 첫 메시지
     */
    public String getFirstMessage() {
        if (this.firstMessages.isEmpty()) {
            return "";
        }
        
        int randomIndex = (int) (Math.random() * this.firstMessages.size());
        return this.firstMessages.get(randomIndex);
    }
}