package com.licky.riotleaderboard.exception;

import com.licky.riotleaderboard.dto.ApiResponse;
import com.licky.riotleaderboard.util.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerNotFound(
            PlayerNotFoundException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                "error",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(SoloDuoRankNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleSoloDuoRankNotFound(
            SoloDuoRankNotFoundException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                "error",
                ex.getMessage(),
                null
        );
    }
}
