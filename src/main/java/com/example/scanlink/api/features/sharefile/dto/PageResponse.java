package com.example.scanlink.api.features.sharefile.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"content", "pageable", "totalElements", "totalPages", "last"})
public class PageResponse<T> {
    private List<T> content;
    private PageableInfo pageable;
    private long totalElements;
    private int totalPages;
    private boolean last;

    @Data
    @Builder
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageable(PageableInfo.builder()
                        .pageNumber(page.getNumber())
                        .pageSize(page.getSize())
                        .build())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}