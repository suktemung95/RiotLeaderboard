package com.licky.riotleaderboard.dto;

import jakarta.annotation.Nullable;

public record ApiResponse<T>(
        boolean success,
        String message,
        @Nullable T data
) {}
