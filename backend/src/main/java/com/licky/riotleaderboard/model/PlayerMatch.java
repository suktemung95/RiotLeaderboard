package com.licky.riotleaderboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "player_matches")
public class PlayerMatch {

    @EmbeddedId
    private PlayerMatchId id;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @MapsId("matchId")
    @JoinColumn(name = "match_id")
    private Match match;

    public PlayerMatch() {
    }

    public PlayerMatch(Player player, Match match) {
        this.player = player;
        this.match = match;

        this.id = new PlayerMatchId(
                player.getId(),
                match.getId()
        );
    }

    public PlayerMatchId getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Match getMatch() {
        return match;
    }
}