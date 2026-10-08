package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.dto.riot.Participant;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;

public record PlayerMatchDataResponse(
        String matchId,
        Long gameStartTime,
        Participant participant
) {}
