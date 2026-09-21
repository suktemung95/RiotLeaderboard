package com.licky.riotleaderboard.dto;

public record RiotRankResponse(
        String queueType,
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses
) {}