package com.licky.riotleaderboard.exception;

public class RiotRateLimitException extends RuntimeException {

    public RiotRateLimitException(String retryAfter) {
        super("Riot API rate limit exceeded. Retry after " + retryAfter + " seconds.");
    }
}
