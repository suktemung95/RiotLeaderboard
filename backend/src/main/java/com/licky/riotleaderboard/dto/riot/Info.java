package com.licky.riotleaderboard.dto.riot;

import java.util.List;

public record Info(
        long gameCreation,
        long gameDuration,
        long gameStartTimestamp,
        int queueId,
        List<Participant> participants
) {}