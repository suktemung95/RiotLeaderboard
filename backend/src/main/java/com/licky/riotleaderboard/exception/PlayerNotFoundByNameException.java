package com.licky.riotleaderboard.exception;

public class PlayerNotFoundByNameException extends RuntimeException {
    public PlayerNotFoundByNameException(String region, String gameName, String tagLine)
    {
        super("Player not found in " + region + " by name: " + gameName + "#" + tagLine);
    }
}
