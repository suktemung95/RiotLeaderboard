package com.licky.riotleaderboard.dto;

public record PlayerRequest(
        String region,
        String gameName,
        String tagLine
) {
}
