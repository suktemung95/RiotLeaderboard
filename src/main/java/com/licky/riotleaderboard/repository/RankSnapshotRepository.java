package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.RankSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankSnapshotRepository extends JpaRepository<RankSnapshot, Long> {
}
