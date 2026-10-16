package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.model.Player;

public record PlayerResponse(
    String gameName,
    String tagLine,
    String region
) {
    public PlayerResponse(Player player) {
        this (
                player.getGameName(),
                player.getTagLine(),
                player.getRegion()
        );
    }
}
