package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.MatchResponse;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final RiotApiService riotApiService;

    public MatchService(
            MatchRepository matchRepository,
            RiotApiService riotApiService
    ) {
        this.matchRepository = matchRepository;
        this.riotApiService = riotApiService;
    }
    public MatchResponse getMatch(String matchId) {
        Optional<Match> match = matchRepository.findByRiotMatchId(matchId);
        if (match.isPresent()) {
            return match.get().getRawData();
        }

        // if not in DB, get from Riot API
        MatchResponse matchResponse = riotApiService.getMatchDetails(matchId);

        Match newMatch = new Match(matchResponse);
        matchRepository.save(newMatch);
        return matchResponse;
    }
}
