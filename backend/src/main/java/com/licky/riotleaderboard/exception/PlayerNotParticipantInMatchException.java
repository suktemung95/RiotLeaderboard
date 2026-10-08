package com.licky.riotleaderboard.exception;

public class PlayerNotParticipantInMatchException extends RuntimeException {
    public PlayerNotParticipantInMatchException(String puuid, String matchId) {

        super("Player of id " + puuid + " is not participant in match " + matchId);
    }
}
