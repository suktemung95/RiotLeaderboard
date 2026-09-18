package com.licky.riotleaderboard.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long id) {
        super("PLayer not found with id: " + id);
    }
}
