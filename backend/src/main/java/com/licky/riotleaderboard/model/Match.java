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
    @Column(columnDefinition = "jsonb", nullable = false)
    private MatchResponse rawData;

    public Match() {};

    public Match(String matchId, long gameStartTime, int queueId, LocalDateTime now, MatchResponse matchResponse) {
        this.riotMatchId = matchId;
        this.gameStartTime = gameStartTime;
        this.queueId = queueId;
        this.recordedAt = now;
        this.rawData = matchResponse;
    }

    public void setRiotMatchId(String riotMatchId) {
        this.riotMatchId = riotMatchId;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setRawData(MatchResponse rawData) {
        this.rawData = rawData;
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

    public MatchResponse getRawData() {
        return rawData;
    }
}
