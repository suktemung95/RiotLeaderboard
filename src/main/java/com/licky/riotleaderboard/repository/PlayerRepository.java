package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByPuuid(String puuid);
    Optional<Player> findByPuuid(String puuid);
}
