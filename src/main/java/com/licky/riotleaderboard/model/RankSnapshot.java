package com.licky.riotleaderboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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

}
