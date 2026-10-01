package com.licky.riotleaderboard.dto;

public record PlayerRankResponse(
        String gameName,
        String tagLine,
        String region,
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses
) {}
