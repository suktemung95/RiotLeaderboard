package com.licky.riotleaderboard.dto;

public record PlayerResponse(
        String gameName,
        String tagLine,
        String region,
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses
) {}
