package com.licky.riotleaderboard.exception;

public class InvalidRiotApiTokenException extends RuntimeException {
    public InvalidRiotApiTokenException() {
        super("Riot API Token is invalid or expired");
    }
}
