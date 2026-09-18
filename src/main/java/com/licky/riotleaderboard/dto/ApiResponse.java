package com.licky.riotleaderboard.dto;

public record ApiResponse<T>(
        String status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>("success", message, data);
    }
}
