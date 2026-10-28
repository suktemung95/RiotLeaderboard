package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.model.Player;

public record AddPlayerResult(
        Player player,
        boolean alreadyExists
) {
    public AddPlayerResult (Player player, boolean alreadyExists) {
        this.player = player;
        this.alreadyExists = alreadyExists;
    }
}
