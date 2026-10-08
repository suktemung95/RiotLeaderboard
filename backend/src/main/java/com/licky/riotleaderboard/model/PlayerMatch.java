package com.licky.riotleaderboard.model;

import com.licky.riotleaderboard.dto.riot.Participant;
import com.licky.riotleaderboard.exception.PlayerNotParticipantInMatchException;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Participant participant;

    public PlayerMatch() {
    }

    public PlayerMatch(Player player, Match match) {
        this.player = player;
        this.match = match;

        this.id = new PlayerMatchId(
                player.getId(),
                match.getId()
        );

        this.participant = match.getRawData().info().participants()
                .stream()
                .filter(p -> p.puuid().equals(player.getPuuid()))
                .findFirst()
                .orElseThrow(() -> new PlayerNotParticipantInMatchException(player.getPuuid(), match.getRiotMatchId()));
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

    public Participant getParticipant() {
        return participant;
    }
}