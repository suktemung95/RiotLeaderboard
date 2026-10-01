package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.*;
import com.licky.riotleaderboard.exception.PlayerNotFoundByIdException;
import com.licky.riotleaderboard.exception.PlayerNotFoundByNameException;
import com.licky.riotleaderboard.exception.RankSnapshotNotFoundException;
import com.licky.riotleaderboard.exception.SoloDuoRankNotFoundException;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.RankSnapshot;
import com.licky.riotleaderboard.repository.PlayerRepository;
import com.licky.riotleaderboard.repository.RankSnapshotRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public Player addPlayer(PlayerResponse request) {
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

        getSoloDuoRank(savedPlayer.getPuuid(), savedPlayer.getRegion())
            .ifPresent(rank -> saveRankSnapshot(savedPlayer, rank));

        return savedPlayer;
    }

    public List<PlayerRankResponse> getLeaderboard() {
        List<Player> players = playerRepository.findAll();

        List<PlayerRankResponse> responses = new ArrayList<>();

        for (Player player : players) {
            Optional<RankSnapshot> latestSnapshot =
                    rankSnapshotRepository
                            .findTopByPlayerOrderByRecordedAtDesc(player);

            if ( latestSnapshot.isPresent() ){
                RankSnapshot rankSnapshot = latestSnapshot.get();
                PlayerRankResponse response = new PlayerRankResponse(
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

        responses.sort(
                Comparator.comparingInt(
                                (PlayerRankResponse response) -> tierValue(response.tier())
                        )
                        .thenComparingInt(
                                response -> rankValue(response.rank())
                        )
                        .thenComparingInt(
                                PlayerRankResponse::leaguePoints
                        )
                        .reversed()
        );

        return responses;
    }

    public List<RankSnapshotResponse> getPlayerHistory(Long id) {

         Player player = playerRepository.findById(id)
                 .orElseThrow(() -> new PlayerNotFoundByIdException(id));

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

    public PlayerResponse getPlayerByName(String region, String gameName, String tagLine) {
        Player player = playerRepository.findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                region, gameName, tagLine
        )
                .orElseThrow(() -> new PlayerNotFoundByNameException(region, gameName, tagLine));


        return playerToResponse(player);
    }

    public RankSnapshot getLatestRankSnapshot(Player player) {
        return rankSnapshotRepository
                .findTopByPlayerOrderByRecordedAtDesc(player)
                .orElse(null);
    }

    public RankSnapshotResponse refreshPlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundByIdException(id));

        return getSoloDuoRank(player.getPuuid(), player.getRegion())
            .map(rank -> saveRankSnapshot(player, rank))
            .map(this::toRankSnapshotResponse)
            .orElseThrow(() -> new SoloDuoRankNotFoundException(id));
    }

    private RankSnapshot saveRankSnapshot(Player player, RiotRankResponse rank) {
        RankSnapshot snapshot = new RankSnapshot();

        snapshot.setPlayer(player);
        snapshot.setTier(rank.tier());
        snapshot.setRank(rank.rank());
        snapshot.setLeaguePoints(rank.leaguePoints());
        snapshot.setWins(rank.wins());
        snapshot.setLosses(rank.losses());
        snapshot.setRecordedAt(LocalDateTime.now());

        return rankSnapshotRepository.save(snapshot);
    }

    private RankSnapshotResponse toRankSnapshotResponse(RankSnapshot snapshot) {
        return new RankSnapshotResponse(
                snapshot.getTier(),
                snapshot.getRank(),
                snapshot.getLeaguePoints(),
                snapshot.getWins(),
                snapshot.getLosses(),
                snapshot.getRecordedAt()
        );
    }

    private Optional<RiotRankResponse> getSoloDuoRank(String puuid, String region) {
        return riotApiService.getRankedStats(
                        puuid,
                        region
                )
                .stream()
                .filter(rank -> rank.queueType().equals("RANKED_SOLO_5x5"))
                .findFirst();
    }

    @Scheduled(fixedRate = 900000)
    public void refreshAll() {
        List<Player> players = playerRepository.findAll();
        List<String> errors = new ArrayList<>();

        for (Player player : players) {

            try {
                refreshPlayerIfChanged(player);
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }

        System.out.println(errors);
    }

    private void refreshPlayerIfChanged(Player player) {
        RiotRankResponse newRank =
                getSoloDuoRank(player.getPuuid(), player.getRegion())
                        .orElseThrow(
                                () -> new SoloDuoRankNotFoundException(player.getId())
                        );

        RankSnapshot latestRankSnapshot = rankSnapshotRepository
                .findTopByPlayerOrderByRecordedAtDesc(player)
                .orElseThrow(() -> new RankSnapshotNotFoundException(player.getId()));

        if (
                latestRankSnapshot == null
                || isRankChanged(latestRankSnapshot, newRank)
        ) {
            saveRankSnapshot(player, newRank);
        }
    }

    private boolean isRankChanged(
            RankSnapshot latestRankSnapshot,
            RiotRankResponse newRank) {
        return (
                !Objects.equals(newRank.tier(), latestRankSnapshot.getTier()) ||
                !Objects.equals(newRank.rank(), latestRankSnapshot.getRank()) ||
                !Objects.equals(newRank.leaguePoints(), latestRankSnapshot.getLeaguePoints())
        );
    }

    private int tierValue(String tier) {
        return switch (tier.toUpperCase()) {
            case "IRON" -> 1;
            case "BRONZE" -> 2;
            case "SILVER" -> 3;
            case "GOLD" -> 4;
            case "PLATINUM" -> 5;
            case "EMERALD" -> 6;
            case "DIAMOND" -> 7;
            case "MASTER" -> 8;
            case "GRANDMASTER" -> 9;
            case "CHALLENGER" -> 10;
            default -> 0;
        };
    }

    private int rankValue(String rank) {
        return switch (rank.toUpperCase()) {
            case "IV" -> 1;
            case "III" -> 2;
            case "II" -> 3;
            case "I" -> 4;
            default -> 0;
        };
    }

    private PlayerResponse playerToResponse(Player player) {
        return new PlayerResponse(
                player.getGameName(),
                player.getTagLine(),
                player.getRegion()
        );
    }

    private RankSnapshotResponse rankSnapshotToResponse(RankSnapshot snapshot) {
        return new RankSnapshotResponse(
                snapshot.getTier(),
                snapshot.getRank(),
                snapshot.getLeaguePoints(),
                snapshot.getWins(),
                snapshot.getLosses(),
                snapshot.getRecordedAt()
        );
    }

    public PlayerOverviewResponse getPlayerOverview(
            PlayerRequest playerRequest
    ) {
        Player p = playerRepository.findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                playerRequest.region(), playerRequest.gameName(), playerRequest.tagLine()
        )
                .orElseThrow(() -> new PlayerNotFoundByNameException(
                        playerRequest.region(), playerRequest.gameName(), playerRequest.tagLine()
                ));

        RankSnapshot rs = rankSnapshotRepository
                .findTopByPlayerOrderByRecordedAtDesc(p)
                .orElseThrow(() -> new RankSnapshotNotFoundException(p.getId()));

        PlayerResponse pr = playerToResponse(p);
        RankSnapshotResponse rsr = rankSnapshotToResponse(rs);

        return new PlayerOverviewResponse(pr, rsr);

    }
}
