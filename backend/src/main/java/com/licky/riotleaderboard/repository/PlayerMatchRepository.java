package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.PlayerMatch;
import com.licky.riotleaderboard.model.PlayerMatchId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerMatchRepository extends JpaRepository<PlayerMatch, PlayerMatchId> {
}
