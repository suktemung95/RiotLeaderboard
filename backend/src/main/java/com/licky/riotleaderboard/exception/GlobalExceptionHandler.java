package com.licky.riotleaderboard.exception;

import com.licky.riotleaderboard.dto.ApiResponse;
import com.licky.riotleaderboard.util.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(PlayerNotFoundByIdException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerNotFoundById(
            PlayerNotFoundByIdException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                false,
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
                false,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(InvalidRiotApiTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRiotApiTokenException (
            InvalidRiotApiTokenException ex
    ) {
        return ApiResponses.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                false,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(PlayerNotFoundByNameException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerNotFoundByName(
            PlayerNotFoundByNameException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                false,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RankSnapshotNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRankSnapshotNotFoundException(
            RankSnapshotNotFoundException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                false,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(PlayerNotParticipantInMatchException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerNotParticipantInMatchException(
            PlayerNotParticipantInMatchException ex
    ) {
        return ApiResponses.build(
                HttpStatus.NOT_FOUND,
                false,
                ex.getMessage(),
                null
        );
    }
}
