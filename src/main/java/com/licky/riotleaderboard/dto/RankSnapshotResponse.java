package com.licky.riotleaderboard.dto;

import java.time.LocalDateTime;

public record RankSnapshotResponse(
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses,
        LocalDateTime recordedAt
) { }
