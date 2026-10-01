package com.licky.riotleaderboard.exception;

public class PlayerNotFoundByIdException extends RuntimeException {
    public PlayerNotFoundByIdException(Long id) {
        super("Player not found with id: " + id);
    }
}
