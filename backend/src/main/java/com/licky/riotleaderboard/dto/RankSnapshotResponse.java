package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.model.RankSnapshot;

import java.time.LocalDateTime;

public record RankSnapshotResponse(
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses,
        LocalDateTime recordedAt
) {

    public RankSnapshotResponse(RankSnapshot rs) {
        this(
                rs.getTier(),
                rs.getRank(),
                rs.getLeaguePoints(),
                rs.getWins(),
                rs.getLosses(),
                rs.getRecordedAt()
        );
    }
}
