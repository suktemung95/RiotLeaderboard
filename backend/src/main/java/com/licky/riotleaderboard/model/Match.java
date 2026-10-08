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

    public Match() {};

    public Match(MatchResponse matchResponse) {
        this.riotMatchId = matchResponse.metadata().matchId();
        this.recordedAt = LocalDateTime.now();
        this.rawData = matchResponse;
    }

    public Long getId() {
        return id;
    }

    public String getRiotMatchId() {
        return riotMatchId;
    }

    public LocalDateTime getRecorded_at() {
        return recordedAt;
    }

    public MatchResponse getRawData() {
        return rawData;
    }
}
