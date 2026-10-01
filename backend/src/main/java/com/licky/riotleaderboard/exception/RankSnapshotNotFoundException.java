package com.licky.riotleaderboard.exception;

public class RankSnapshotNotFoundException extends RuntimeException {
    public RankSnapshotNotFoundException(Long id) {
        super("Rank snapshot not found for player id: " + id);
    }
}
