package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.*;
import com.licky.riotleaderboard.exception.PlayerNotFoundException;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.RankSnapshot;
import com.licky.riotleaderboard.repository.PlayerRepository;
import com.licky.riotleaderboard.repository.RankSnapshotRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final RankSnapshotRepository rankSnapshotRepository;
    private final RiotApiService riotApiService;

    public PlayerService(
            PlayerRepository playerRepository,
            RankSnapshotRepository rankSnapshotRepository,
            RiotApiService riotApiService
    ) {
        this.playerRepository = playerRepository;
        this.rankSnapshotRepository = rankSnapshotRepository;
        this.riotApiService = riotApiService;
    }

    public Player addPlayer(AddPlayerRequest request) {
        RiotAccountResponse riotAccount =
                riotApiService.getAccount(request.gameName(), request.tagLine());

        Player player;

        Optional<Player> optionalPlayer = playerRepository.findByPuuid(riotAccount.puuid());

        if ( optionalPlayer.isPresent() ) {
            player = optionalPlayer.get();
        } else {
            player = new Player();
            player.setPuuid(riotAccount.puuid());
        }

        // refresh gameName and tagLine and region
        player.setGameName(riotAccount.gameName());
        player.setTagLine(riotAccount.tagLine());
        player.setRegion(request.region());


        Player savedPlayer = playerRepository.save(player);

        List<RiotRankResponse> rankedStats =
                riotApiService.getRankedStats(
                        savedPlayer.getPuuid(),
                        savedPlayer.getRegion()
                );

        rankedStats.stream()
                .filter(rank -> rank.queueType().equals("RANKED_SOLO_5x5"))
                .findFirst()
                .ifPresent(rank -> {
                    RankSnapshot snapshot = new RankSnapshot();

                    snapshot.setPlayer(savedPlayer);
                    snapshot.setTier(rank.tier());
                    snapshot.setRank(rank.rank());
                    snapshot.setLeaguePoints(rank.leaguePoints());
                    snapshot.setWins(rank.wins());
                    snapshot.setLosses(rank.losses());
                    snapshot.setRecordedAt(LocalDateTime.now());

                    rankSnapshotRepository.save(snapshot);
                });

        return savedPlayer;
    }

    public List<PlayerResponse> getLeaderboard() {
        List<Player> players = playerRepository.findAll();

        List<PlayerResponse> responses = new ArrayList<>();

        for (Player player : players) {
            Optional<RankSnapshot> latestSnapshot =
                    rankSnapshotRepository
                            .findTopByPlayerOrderByRecordedAtDesc(player);

            if ( latestSnapshot.isPresent() ){
                RankSnapshot rankSnapshot = latestSnapshot.get();
                PlayerResponse response = new PlayerResponse(
                        player.getGameName(),
                        player.getTagLine(),
                        player.getRegion(),
                        rankSnapshot.getTier(),
                        rankSnapshot.getRank(),
                        rankSnapshot.getLeaguePoints(),
                        rankSnapshot.getWins(),
                        rankSnapshot.getLosses()
                );

                responses.add(response);
            }
        }

        return responses;
    }

    public List<RankSnapshotResponse> getPlayerHistory(Long id) {

         Player player = playerRepository.findById(id)
                 .orElseThrow(() -> new PlayerNotFoundException(id));

         return rankSnapshotRepository
                 .findByPlayerOrderByRecordedAtDesc(player)
                 .stream()
                 .map(s -> new RankSnapshotResponse(
                         s.getTier(),
                         s.getRank(),
                         s.getLeaguePoints(),
                         s.getWins(),
                         s.getLosses(),
                         s.getRecordedAt()
                 ))
                 .toList();
    }
}
