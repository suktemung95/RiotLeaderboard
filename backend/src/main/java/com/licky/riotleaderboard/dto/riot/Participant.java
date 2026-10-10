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
        boolean win,
        int item0,
        int item1,
        int item2,
        int item3,
        int item4,
        int item5,
        int item6,
        String lane,
        int largestMultiKill,
        String role,
        int teamId,
        int totalDamageDealt,
        int totalDamageDealtToChampions,
        int totalDamageShieldedOnTeammates,
        int totalDamageTaken,
        int visionScore
) {
}
