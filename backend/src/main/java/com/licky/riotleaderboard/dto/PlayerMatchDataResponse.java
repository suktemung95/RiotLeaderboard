package com.licky.riotleaderboard.dto;

import com.licky.riotleaderboard.dto.riot.Info;
import com.licky.riotleaderboard.dto.riot.Participant;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;

public record PlayerMatchDataResponse(
        String matchId,
        Info info,
        Participant participant
) {}
