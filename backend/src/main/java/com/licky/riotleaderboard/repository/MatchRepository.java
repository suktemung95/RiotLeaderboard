package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
