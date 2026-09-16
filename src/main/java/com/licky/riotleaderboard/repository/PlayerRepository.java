package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
