package com.example.hangeulhunters.application.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageResponse<T> {
    @Schema(description = "페이지 내용", required = true)
    private List<T> content;

    @Schema(description = "현재 페이지 번호", required = true)
    private int pageNumber;

    @Schema(description = "페이지 크기", required = true)
    private int pageSize;

    @Schema(description = "총 요소 수", required = true)
    private long totalElements;

    @Schema(description = "총 페이지 수", required = true)
    private int totalPages;

    @Schema(description = "첫 페이지 여부", required = true)
    private boolean isFirst;

    @Schema(description = "마지막 페이지 여부", required = true)
    private boolean isLast;

    /**
     * Page 객체와 변환된 content로 응답 생성 - 제네릭 static 메서드
     */
    public static <T> PageResponse<T> of(Page<?> page, List<T> convertedContent) {
        return PageResponse.<T>builder()
                .content(convertedContent)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    /**
     * Page 객체와 변환된 content로 응답 생성
     */
    public static <S, T> PageResponse<T> of(Page<S> page, Function<S, T> converter) {
        List<T> convertedContent = page.getContent().stream()
                .map(converter)
                .toList();

        return of(page, convertedContent);
    }
}
