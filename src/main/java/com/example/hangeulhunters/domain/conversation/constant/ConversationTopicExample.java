package com.example.hangeulhunters.domain.conversation.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConversationTopicExample {
    after_work_escape_mode("After-Work Escape Mode", "Your Korean team leader", "Junior employee in a Korean company"),
    could_you_soften_your_tone("“Could You Soften Your Tone…?”", "Korean boyfriend/girlfriend", "Partner in a Korean–non-Korean couple"),
    midnight_mom_energy("Midnight Mom Energy", "Korean host / house “mom”", "Student or worker living in Korea"),
    bias_talk_irl("Bias Talk IRL", "Korean friend who also likes K-pop", "K-pop fan learning Korean"),;

    private final String topicName;
    private final String aiRole;
    private final String userRole;

    public static ConversationTopicExample getTopicExampleByName(String topicName) {
        for (ConversationTopicExample topic : ConversationTopicExample.values()) {
            if (topic.getTopicName().equals(topicName)) {
                return topic;
            }
        }
        return null;
    }
}
