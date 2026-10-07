package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.dto.riot.Info;
import com.licky.riotleaderboard.dto.riot.Metadata;

public record MatchResponse(
        Metadata metadata,
        Info info
) {
}
