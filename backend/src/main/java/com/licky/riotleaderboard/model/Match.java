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
    private String riotMatchId;
    private long gameStartTime;
    private int queueId;
    private LocalDateTime recordedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private MatchResponse rawData;

    public Match() {};

    public Match(String matchId, long gameStartTime, int queueId, LocalDateTime now, MatchResponse matchResponse) {
        this.riotMatchId = matchId;
        this.gameStartTime = gameStartTime;
        this.queueId = queueId;
        this.recordedAt = now;
        this.rawData = matchResponse;
    }

    public Long getId() {
        return id;
    }

    public String getRiot_match_id() {
        return riotMatchId;
    }

    public long getGame_start_time() {
        return gameStartTime;
    }

    public int getQueue_id() {
        return queueId;
    }

    public LocalDateTime getRecorded_at() {
        return recordedAt;
    }
}
