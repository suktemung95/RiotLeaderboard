package com.licky.riotleaderboard.exception;

public class PlayerNotFoundByPuuidException extends RuntimeException {
    public PlayerNotFoundByPuuidException(String id) {
        super("Player not found with id: " + id);
    }
}
