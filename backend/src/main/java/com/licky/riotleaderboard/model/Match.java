package com.licky.riotleaderboard.model;

import com.licky.riotleaderboard.dto.MatchResponse;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String riotMatchId;
    private LocalDateTime recordedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private MatchResponse rawData;

    @Column(name = "game_start_timestamp", nullable = false)
    private Long gameStartTimestamp;

    public Match() {};

    public Match(MatchResponse matchResponse) {
        this.riotMatchId = matchResponse.metadata().matchId();
        this.recordedAt = LocalDateTime.now();
        this.rawData = matchResponse;
        this.gameStartTimestamp = matchResponse.info().gameStartTimestamp();
    }

    public Long getId() {
        return id;
    }

    public String getRiotMatchId() {
        return riotMatchId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public MatchResponse getRawData() {
        return rawData;
    }

    public Long getGameStartTimestamp() {
        return gameStartTimestamp;
    }
}
