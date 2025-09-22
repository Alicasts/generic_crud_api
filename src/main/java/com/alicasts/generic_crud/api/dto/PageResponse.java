package com.alicasts.generic_crud.api.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean first,
                              boolean last, String sort) {

    public static <T> PageResponse<T> from(Page<?> page, List<T> content) {
        String sort = page.getSort().isUnsorted() ? "" : page.getSort().toString();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                sort
        );
    }
}
