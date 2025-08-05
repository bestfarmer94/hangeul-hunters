package com.example.hangeulhunters.infrastructure.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * OffsetDateTime과 LocalDateTime 간 변환을 위한 유틸리티 클래스
 */
public class DateTimeUtil {

    private static final ZoneOffset ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());

    /**
     * LocalDateTime을 시스템 기본 시간대 오프셋을 사용하여 OffsetDateTime으로 변환
     *
     * @param localDateTime 변환할 LocalDateTime
     * @return 시스템 기본 시간대 오프셋이 적용된 OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZONE_OFFSET);
    }

    /**
     * OffsetDateTime을 LocalDateTime으로 변환
     *
     * @param offsetDateTime 변환할 OffsetDateTime
     * @return LocalDateTime (시간대 정보는 손실됨)
     */
    public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }

    /**
     * 시스템 기본 시간대 오프셋이 적용된 현재 OffsetDateTime 반환
     *
     * @return 현재 OffsetDateTime
     */
    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}