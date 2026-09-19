package com.licky.riotleaderboard.exception;

public class SoloDuoRankNotFoundException extends RuntimeException {
    public SoloDuoRankNotFoundException(Long id) {
        super("Solo / Duo rank not found for id: " + id);
    }
}
