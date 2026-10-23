package com.licky.riotleaderboard.exception;

import com.licky.riotleaderboard.model.Player;

public class RefreshCooldownException extends RuntimeException {
    public RefreshCooldownException(Player player) {
        super("Player was refreshed too recently. Try again shortly.");
    }
}
