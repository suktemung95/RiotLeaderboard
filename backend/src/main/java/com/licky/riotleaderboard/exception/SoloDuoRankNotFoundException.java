package com.licky.riotleaderboard.exception;

public class SoloDuoRankNotFoundException extends RuntimeException {
    public SoloDuoRankNotFoundException(String puuid) {
        super("Solo / Duo rank not found for id: " + puuid);
    }
}
