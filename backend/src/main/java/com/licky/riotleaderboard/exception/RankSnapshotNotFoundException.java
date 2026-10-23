package com.licky.riotleaderboard.exception;

public class RankSnapshotNotFoundException extends RuntimeException {
    public RankSnapshotNotFoundException(String gameName, String tagLine, String region) {
        super("Rank snapshot not found for player: "+ region + " " + gameName + "#" + tagLine);
    }
}
