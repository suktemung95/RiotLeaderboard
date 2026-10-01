package com.licky.riotleaderboard.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlayerMatchId implements Serializable {

    private Long playerId;
    private Long matchId;

    public PlayerMatchId() {
    }

    public PlayerMatchId(Long playerId, Long matchId) {
        this.playerId = playerId;
        this.matchId = matchId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getMatchId() {
        return matchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PlayerMatchId that)) {
            return false;
        }

        return Objects.equals(playerId, that.playerId)
                && Objects.equals(matchId, that.matchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, matchId);
    }
}