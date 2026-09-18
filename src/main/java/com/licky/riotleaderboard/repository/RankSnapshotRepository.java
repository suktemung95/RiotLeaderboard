package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.RankSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankSnapshotRepository extends JpaRepository<RankSnapshot, Long> {
    Optional<RankSnapshot> findTopByPlayerOrderByRecordedAtDesc(Player player);
}
