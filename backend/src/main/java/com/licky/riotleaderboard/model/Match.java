package com.licky.riotleaderboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String riot_match_id;
    private String game_start_time;
    private String queue_id;
    private LocalDateTime recorded_at;

    public Long getId() {
        return id;
    }

    public String getRiot_match_id() {
        return riot_match_id;
    }

    public String getGame_start_time() {
        return game_start_time;
    }

    public String getQueue_id() {
        return queue_id;
    }

    public LocalDateTime getRecorded_at() {
        return recorded_at;
    }
}
