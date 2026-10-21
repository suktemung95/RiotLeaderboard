package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.model.Player;

public record PlayerRequest(
        String region,
        String gameName,
        String tagLine
) {
    public PlayerRequest(Player player) {
        this (
                player.getRegion(),
                player.getGameName(),
                player.getTagLine()
        );
    }
}
