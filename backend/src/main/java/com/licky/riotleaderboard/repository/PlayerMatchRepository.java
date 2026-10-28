package com.licky.riotleaderboard.repository;

import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.PlayerMatch;
import com.licky.riotleaderboard.model.PlayerMatchId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlayerMatchRepository extends JpaRepository<PlayerMatch, PlayerMatchId> {
    List<PlayerMatch> findByPlayer(Player player);
    List<PlayerMatch> findTop21ByPlayerOrderByMatch_GameStartTimestampDesc(Player player);
    List<PlayerMatch>
    findByPlayerAndMatch_RiotMatchIdInOrderByMatch_GameStartTimestampDesc(
            Player player,
            List<String> riotMatchIds
    );
    List<PlayerMatch> findTop21ByPlayerAndMatch_GameStartTimestampLessThanOrderByMatch_GameStartTimestampDesc(
            Player player,
            Long before
    );
}
