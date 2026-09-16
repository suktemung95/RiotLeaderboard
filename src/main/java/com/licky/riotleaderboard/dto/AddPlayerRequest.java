package com.licky.riotleaderboard.dto;

public record AddPlayerRequest (
    String gameName,
    String tagLine,
    String region
) {}
