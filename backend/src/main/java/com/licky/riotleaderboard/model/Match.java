package com.licky.riotleaderboard.model;

import jakarta.persistence.*;

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

    public Match() {};

    public Match(String matchId, long gameStartTime, int queueId, LocalDateTime now) {
        this.riotMatchId = matchId;
        this.gameStartTime = gameStartTime;
        this.queueId = queueId;
        this.recordedAt = now;
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
