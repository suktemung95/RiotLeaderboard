package com.licky.riotleaderboard.dto;

public record PlayerOverviewResponse(
        PlayerResponse playerResponse,
        RankSnapshotResponse rankSnapshotResponse
) {
}
