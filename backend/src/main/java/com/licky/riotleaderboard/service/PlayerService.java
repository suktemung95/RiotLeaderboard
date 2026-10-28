package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.*;
import com.licky.riotleaderboard.exception.PlayerNotFoundByNameException;
import com.licky.riotleaderboard.exception.RankSnapshotNotFoundException;
import com.licky.riotleaderboard.exception.RefreshCooldownException;
import com.licky.riotleaderboard.exception.SoloDuoRankNotFoundException;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.PlayerMatch;
import com.licky.riotleaderboard.model.RankSnapshot;
import com.licky.riotleaderboard.repository.MatchRepository;
import com.licky.riotleaderboard.repository.PlayerMatchRepository;
import com.licky.riotleaderboard.repository.PlayerRepository;
import com.licky.riotleaderboard.repository.RankSnapshotRepository;
import com.licky.riotleaderboard.riot.RiotApiService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final PlayerMatchRepository playerMatchRepository;
    private final RankSnapshotRepository rankSnapshotRepository;
    private final RiotApiService riotApiService;
    private final TaskExecutor taskExecutor;

    public PlayerService(
            PlayerRepository playerRepository,
            MatchRepository matchRepository,
            PlayerMatchRepository playerMatchRepository,
            RankSnapshotRepository rankSnapshotRepository,
            RiotApiService riotApiService,
            TaskExecutor taskExecutor
    ) {
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.playerMatchRepository = playerMatchRepository;
        this.rankSnapshotRepository = rankSnapshotRepository;
        this.riotApiService = riotApiService;
        this.taskExecutor = taskExecutor;
    }

    public AddPlayerResult addPlayer(PlayerResponse request) {
        RiotAccountResponse riotAccount =
                riotApiService.getAccount(request.gameName(), request.tagLine());

        Player player;
        boolean exists = false;

        Optional<Player> optionalPlayer = playerRepository.findByPuuid(riotAccount.puuid());

        if ( optionalPlayer.isPresent() ) {
            player = optionalPlayer.get();
            exists = true;
        } else {
            player = new Player();
            player.setPuuid(riotAccount.puuid());
        }

        // refresh gameName and tagLine and region
        player.setGameName(riotAccount.gameName());
        player.setTagLine(riotAccount.tagLine());
        player.setRegion(request.region());

        Player savedPlayer = playerRepository.save(player);

        refreshSoloDuoRank(savedPlayer.getPuuid(), savedPlayer.getRegion())
            .ifPresent(rank ->
                    rankSnapshotRepository.save(new RankSnapshot(savedPlayer, rank))
            );

        return new AddPlayerResult(savedPlayer, exists);
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

    public List<RankSnapshotResponse> getPlayerHistory(PlayerRequest pr) {
        Player player = findPlayerByPlayerRequest(pr);

        List<RankSnapshot> rankHistory = rankSnapshotRepository
                 .findByPlayerOrderByRecordedAtDesc(player);

        if (rankHistory.isEmpty()) {
            taskExecutor.execute(() -> refreshRank(player));
            return List.of();
        }

        if (rankHistory.getFirst()
                .getRecordedAt()
                .isBefore(LocalDateTime.now().minusMinutes(30))) {

            taskExecutor.execute(() -> refreshRank(player));
        }

        return rankHistory.stream()
                .map(RankSnapshotResponse::new)
                .toList();
    }

    private Optional<RiotRankResponse> refreshSoloDuoRank(String puuid, String region) {
        return riotApiService.getRankedStats(
                        puuid,
                        region
                )
                .stream()
                .filter(rank -> rank.queueType().equals("RANKED_SOLO_5x5"))
                .findFirst();
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

    public PlayerOverviewResponse getPlayerOverview(
            PlayerRequest playerRequest
    ) {
        Player p = findPlayerByPlayerRequest(playerRequest);
        RankSnapshot rs = rankSnapshotRepository
                .findTopByPlayerOrderByRecordedAtDesc(p)
                .orElseThrow(() -> new RankSnapshotNotFoundException(
                        p.getGameName(),
                        p.getTagLine(),
                        p.getRegion()
                ));

        if (rs.getRecordedAt().isBefore(LocalDateTime.now().minusMinutes(30)) ) {
            taskExecutor.execute(() -> refreshRank(p));
        }

        PlayerResponse pr = new PlayerResponse(p);
        RankSnapshotResponse rsr = new RankSnapshotResponse(rs);

        return new PlayerOverviewResponse(pr, rsr);
    }

    public List<PlayerMatchDataResponse> getPlayerMatches(PlayerRequest playerRequest, Long before) {

        Player player = findPlayerByPlayerRequest(playerRequest);

        List<PlayerMatch> playerMatches;

        if (before == null) {
            playerMatches =
                    playerMatchRepository
                            .findTop20ByPlayerOrderByMatch_GameStartTimestampDesc(player);
        } else {
            playerMatches =
                    playerMatchRepository
                            .findTop20ByPlayerAndMatch_GameStartTimestampLessThanOrderByMatch_GameStartTimestampDesc(
                                    player,
                                    before
                            );
        }


        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

        if (player.getMatchesLastRefreshedAt() == null
                || player.getMatchesLastRefreshedAt().isBefore(cutoff)) {

            taskExecutor.execute(() -> refreshMatches(player));
        }

        return playerMatchesToDataResponse(playerMatches);
    }

    private Player findPlayerByPlayerRequest(PlayerRequest playerRequest) {
        return playerRepository.findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                        playerRequest.region(),
                        playerRequest.gameName(),
                        playerRequest.tagLine()
                )
                .orElseThrow(() -> new PlayerNotFoundByNameException(
                        playerRequest.region(),
                        playerRequest.gameName(),
                        playerRequest.tagLine()
                ));
    }

    private RankSnapshotResponse refreshRank(Player player) {
        String puuid = player.getPuuid();

        RiotRankResponse riotRankResponse = refreshSoloDuoRank(puuid, player.getRegion())
                .orElseThrow(() -> new SoloDuoRankNotFoundException(puuid));

        RankSnapshot newRankSnapshot = new RankSnapshot(player, riotRankResponse);

        Optional<RankSnapshot> prevRankSnapshot = rankSnapshotRepository.findTopByPlayerOrderByRecordedAtDesc(player);

        RankSnapshot savedRankSnapshot;

        // if prev exists and not same rank
        if (prevRankSnapshot.isEmpty() || !prevRankSnapshot.get().hasSameRank(newRankSnapshot)) {
            savedRankSnapshot =
                    rankSnapshotRepository.save(newRankSnapshot);
        // if exists and same rank
        } else {

            RankSnapshot existingSnapshot = prevRankSnapshot.get();
            existingSnapshot.setRecordedAt(LocalDateTime.now());
            savedRankSnapshot =
                    rankSnapshotRepository.save(existingSnapshot);
        }

        return new RankSnapshotResponse(savedRankSnapshot);
    }

    private List<PlayerMatchDataResponse> refreshMatches(Player player) {

        String puuid = player.getPuuid();
        List<String> fetchedMatchIds = riotApiService.getMatchIds(puuid);
        List<Match> existingMatches =
                matchRepository.findByRiotMatchIdIn(fetchedMatchIds);

        Set<String> existingMatchIds = new HashSet<>();

        for (Match match : existingMatches) {
            existingMatchIds.add(match.getRiotMatchId());
        }

        // fetch data for new matches
        List<Match> newMatches = new ArrayList<>();

        for (String matchId : fetchedMatchIds) {
            if (!existingMatchIds.contains(matchId)) {

                MatchResponse matchResponse =
                        riotApiService.getMatchDetails(matchId);

                Match match = new Match(matchResponse);

                newMatches.add(match);
            }
        }

        newMatches = matchRepository.saveAll(newMatches);

        // combine all matches
        List<Match> allMatches = new ArrayList<>(existingMatches);
        allMatches.addAll(newMatches);

        synchronizePlayerMatches(player, allMatches);

        List<PlayerMatch> playerMatches =
                playerMatchRepository.findByPlayerAndMatch_RiotMatchIdInOrderByMatch_GameStartTimestampDesc(
                    player,
                    fetchedMatchIds
            );

        player.setMatchesLastRefreshedAt(LocalDateTime.now());
        playerRepository.save(player);

        return playerMatchesToDataResponse(playerMatches);
    }

    private void synchronizePlayerMatches(Player player, List<Match> matches) {
        // find existing playerMatch relationships
        List<PlayerMatch> existingPlayerMatches =
                playerMatchRepository.findByPlayer(player);

        Set<Long> linkedMatchIds = new HashSet<>();

        for (PlayerMatch playerMatch : existingPlayerMatches) {
            linkedMatchIds.add(
                    playerMatch.getMatch().getId()
            );
        }

        // create missing relationships
        List<PlayerMatch> newPlayerMatches = new ArrayList<>();

        for (Match match : matches) {
            if (!linkedMatchIds.contains(match.getId())) {
                newPlayerMatches.add(
                        new PlayerMatch(player, match)
                );
            }
        }

        playerMatchRepository.saveAll(newPlayerMatches);
    }

    private List<PlayerMatchDataResponse> playerMatchesToDataResponse(
            List<PlayerMatch> playerMatches
    ) {
        List<PlayerMatchDataResponse> responses = new ArrayList<>();

        for (PlayerMatch playerMatch : playerMatches) {

            Match match = playerMatch.getMatch();
            MatchResponse rawData = match.getRawData();

            responses.add(
                    new PlayerMatchDataResponse(
                            match.getRiotMatchId(),
                            rawData.info(),
                            playerMatch.getParticipant(),
                            match.getRecordedAt()
                    )
            );
        }

        return responses;
    }

    @Transactional
    public PlayerRefreshResponse refreshPlayerData(PlayerRequest playerRequest) {

        // refresh rank
        Player player = findPlayerByPlayerRequest(playerRequest);

        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(30);

        if (player.getLastRefreshedAt() != null
                && player.getLastRefreshedAt().isAfter(cutoff)) {
            throw new RefreshCooldownException(player);
        }

        RankSnapshotResponse refreshedRank = refreshRank(player);
        List<PlayerMatchDataResponse> matchResponses = refreshMatches(player);
        PlayerResponse playerResponse = new PlayerResponse(player);

        PlayerOverviewResponse playerOverviewResponse =
                new PlayerOverviewResponse (
                        playerResponse,
                        refreshedRank
                );

        player.setLastRefreshedAt(LocalDateTime.now());
        playerRepository.save(player);

        // Return everything to frontend
        return new PlayerRefreshResponse(
                playerOverviewResponse,
                matchResponses
        );

    }
}