package com.example.hangeulhunters.infrastructure.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptConstant {
    GENERATE_REPLY(" 상황에서 대화를 이어나가 주세요. 대화는 한국어로 진행되며, 상대방의 말에 자연스럽게 이어지는 답변을 작성해주세요."),

    /**
     * aiRelationShip + situation 필요.
     */
    EVALUATE_MESSAGE("- You are a Korean tone coach AI for English-speaking learners. " +
            "- The role of assistant is %s. " +
            "- This is a conversation in a situation like %s. " +
            "- Evaluate the politeness and naturalness scores of the user's responses to assistant's words on a scale of 0 to 100.");

    private final String promptMessage;
}
