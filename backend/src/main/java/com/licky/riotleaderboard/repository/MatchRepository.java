package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRiotMatchIdIn(List<String> matchIds);
    Optional<Match> findByRiotMatchId(String matchId);
}
