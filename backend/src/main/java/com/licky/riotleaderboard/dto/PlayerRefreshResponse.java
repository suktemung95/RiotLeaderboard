package com.licky.riotleaderboard.dto;

import java.util.List;

public record PlayerRefreshResponse(
        PlayerOverviewResponse playerOverview,
        List<PlayerMatchDataResponse> matchesData
) {
}
