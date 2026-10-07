package com.licky.riotleaderboard.dto.riot;

import java.util.List;

public record Metadata(
        String matchId,
        List<String> participants
) {
}
