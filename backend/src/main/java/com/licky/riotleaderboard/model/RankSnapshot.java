package com.licky.riotleaderboard.model;

import com.licky.riotleaderboard.dto.RiotRankResponse;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rank_snapshots")
public class RankSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    private String tier;
    private String rank;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;
    private LocalDateTime recordedAt;

    public RankSnapshot(Player player, RiotRankResponse rank) {
        this.player = player;
        this.tier = rank.tier();
        this.rank = rank.rank();
        this.leaguePoints = rank.leaguePoints();
        this.wins = rank.wins();
        this.losses = rank.losses();
        this.recordedAt = LocalDateTime.now();
    }

    public RankSnapshot() {}

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getLeaguePoints() {
        return leaguePoints;
    }

    public void setLeaguePoints(Integer leaguePoints) {
        this.leaguePoints = leaguePoints;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public boolean hasSameRank(RankSnapshot that) {
        return Objects.equals(this.getTier(), that.getTier()) &&
                Objects.equals(this.getRank(), that.getRank()) &&
                Objects.equals(this.getLeaguePoints(), that.getLeaguePoints()) &&
                Objects.equals(this.getWins(), that.getWins()) &&
                Objects.equals(this.getLosses(), that.getLosses());
    }

}
