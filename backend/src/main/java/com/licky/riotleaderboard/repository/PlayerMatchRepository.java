package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.PlayerMatch;
import com.licky.riotleaderboard.model.PlayerMatchId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerMatchRepository extends JpaRepository<PlayerMatch, PlayerMatchId> {
    List<PlayerMatch> findByPlayer(Player player);
}
