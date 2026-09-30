package com.licky.riotleaderboard.dto;

import jakarta.annotation.Nullable;

public record ApiResponse<T>(
        String status,
        String message,
        @Nullable T data
) {}
