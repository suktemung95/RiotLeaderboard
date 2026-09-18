package com.licky.riotleaderboard.util;

import com.licky.riotleaderboard.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponses {

    public static <T> ResponseEntity<ApiResponse<T>> build(
            HttpStatus status,
            String responseStatus,
            String message,
            T data
    ) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(responseStatus, message, data));
    }
}
