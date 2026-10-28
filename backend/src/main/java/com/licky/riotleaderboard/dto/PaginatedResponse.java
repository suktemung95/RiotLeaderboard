package com.licky.riotleaderboard.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> items,
        Long nextCursor,
        boolean hasMore
) {
}
