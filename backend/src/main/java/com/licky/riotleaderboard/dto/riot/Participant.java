package com.licky.riotleaderboard.dto.riot;

public record Participant(
        String puuid,
        String riotIdGameName,
        String riotIdTagline,
        String championName,
        int kills,
        int deaths,
        int assists,
        int totalMinionsKilled,
        int neutralMinionsKilled,
        boolean win
) {
}
