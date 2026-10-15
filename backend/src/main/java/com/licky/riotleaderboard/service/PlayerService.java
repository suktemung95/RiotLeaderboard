package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.*;
import com.licky.riotleaderboard.exception.PlayerNotFoundByPuuidException;
import com.licky.riotleaderboard.exception.PlayerNotFoundByNameException;
import com.licky.riotleaderboard.exception.RankSnapshotNotFoundException;
import com.licky.riotleaderboard.exception.SoloDuoRankNotFoundException;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.model.PlayerMatch;
import com.licky.riotleaderboard.model.RankSnapshot;
import com.licky.riotleaderboard.repository.MatchRepository;
import com.licky.riotleaderboard.repository.PlayerMatchRepository;
import com.licky.riotleaderboard.repository.PlayerRepository;
import com.licky.riotleaderboard.repository.RankSnapshotRepository;
import org.springframework.scheduling.annotation.Scheduled;
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

    public PlayerService(
            PlayerRepository playerRepository,
            MatchRepository matchRepository,
            PlayerMatchRepository playerMatchRepository,
            RankSnapshotRepository rankSnapshotRepository,
            RiotApiService riotApiService
    ) {
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.playerMatchRepository = playerMatchRepository;
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

        refreshSoloDuoRank(savedPlayer.getPuuid(), savedPlayer.getRegion())
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

    public List<RankSnapshotResponse> getPlayerHistory(PlayerRequest pr) {

         Player player = playerRepository.findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                 pr.region(),
                 pr.gameName(),
                 pr.tagLine()
                 )
                 .orElseThrow(() -> new PlayerNotFoundByNameException(pr));

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

//    public RankSnapshotResponse refreshPlayerRank(Long id) {
//        Player player = playerRepository.findById(id)
//                .orElseThrow(() -> new PlayerNotFoundByIdException(id));
//
//        return refreshSoloDuoRank(player.getPuuid(), player.getRegion())
//            .map(rank -> saveRankSnapshot(player, rank))
//            .map(this::toRankSnapshotResponse)
//            .orElseThrow(() -> new SoloDuoRankNotFoundException(player.getPuuid()));
//    }

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

    private Optional<RiotRankResponse> refreshSoloDuoRank(String puuid, String region) {
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
                refreshSoloDuoRank(player.getPuuid(), player.getRegion())
                        .orElseThrow(
                                () -> new SoloDuoRankNotFoundException(player.getPuuid())
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

    public List<PlayerMatchDataResponse> getPlayerMatches(PlayerRequest playerRequest) {

        Player player = playerRepository
                .findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                        playerRequest.region(),
                        playerRequest.gameName(),
                        playerRequest.tagLine()
                )
                .orElseThrow(() -> new PlayerNotFoundByNameException(
                        playerRequest.region(),
                        playerRequest.gameName(),
                        playerRequest.tagLine()
                ));

        String puuid = player.getPuuid();

        // Get most recent match IDs from Riot
        List<String> fetchedMatchIds = riotApiService.getMatchIds(puuid);

        // Find which matches we already have
        List<Match> existingMatches =
                matchRepository.findByRiotMatchIdIn(fetchedMatchIds);

        Set<String> existingMatchIds = new HashSet<>();

        for (Match match : existingMatches) {
            existingMatchIds.add(match.getRiotMatchId());
        }

        // Determine which Riot matches are missing from our database
        List<String> newMatchIds = new ArrayList<>();

        for (String matchId : fetchedMatchIds) {
            if (!existingMatchIds.contains(matchId)) {
                newMatchIds.add(matchId);
            }
        }

        // Fetch and create missing matches
        List<Match> newMatches = new ArrayList<>();

        for (String matchId : newMatchIds) {
            MatchResponse matchResponse =
                    riotApiService.getMatchDetails(matchId);

            newMatches.add(new Match(matchResponse));
        }

        // Save them before creating PlayerMatch relationships
        newMatches = matchRepository.saveAll(newMatches);

        // All matches relevant to this request
        List<Match> allMatches = new ArrayList<>(existingMatches);
        allMatches.addAll(newMatches);

        // Find which relationships already exist for this player
        Set<Long> existingPlayerMatchIds = new HashSet<>();

        for (PlayerMatch playerMatch :
                playerMatchRepository.findByPlayer(player)) {

            existingPlayerMatchIds.add(
                    playerMatch.getMatch().getId()
            );
        }

        // Create missing PlayerMatch relationships
        List<PlayerMatch> newPlayerMatches = new ArrayList<>();

        for (Match match : allMatches) {
            if (!existingPlayerMatchIds.contains(match.getId())) {
                newPlayerMatches.add(
                        new PlayerMatch(player, match)
                );
            }
        }

        playerMatchRepository.saveAll(newPlayerMatches);

        // Now retrieve the PlayerMatch rows needed for the response
        List<PlayerMatch> playerMatches =
                playerMatchRepository
                        .findByPlayer_GameNameIgnoreCaseAndPlayer_TagLineIgnoreCaseAndPlayer_RegionIgnoreCaseAndMatch_RiotMatchIdIn(
                                playerRequest.gameName(),
                                playerRequest.tagLine(),
                                playerRequest.region(),
                                fetchedMatchIds
                        );

        /*
         * Repository IN queries do not guarantee that the result order
         * matches fetchedMatchIds, so index them by Riot match ID.
         */
        Map<String, PlayerMatch> matchesById = new HashMap<>();

        for (PlayerMatch playerMatch : playerMatches) {
            matchesById.put(
                    playerMatch.getMatch().getRiotMatchId(),
                    playerMatch
            );
        }

        // Build responses in the same order Riot returned the IDs
        List<PlayerMatchDataResponse> responses =
                new ArrayList<>();

        for (String matchId : fetchedMatchIds) {

            PlayerMatch playerMatch = matchesById.get(matchId);

            if (playerMatch == null) {
                continue;
            }

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
    public PlayerRefreshResponse refreshPlayer(PlayerRequest playerRequest) {

        // refresh rank
        Player player = playerRepository.findByRegionIgnoreCaseAndGameNameIgnoreCaseAndTagLineIgnoreCase(
                    playerRequest.region(),
                    playerRequest.gameName(),
                    playerRequest.tagLine()
                )
                .orElseThrow(() -> new PlayerNotFoundByNameException(
                        playerRequest.region(),
                        playerRequest.gameName(),
                        playerRequest.tagLine()
                ));
        String puuid = player.getPuuid();

        // get latest match Ids from Riot
        RiotRankResponse riotRankResponse = refreshSoloDuoRank(puuid, player.getRegion())
                .orElseThrow(() -> new SoloDuoRankNotFoundException(puuid));

        // find which matches are already in DB
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

        for (Match match : allMatches) {

            if (!linkedMatchIds.contains(match.getId())) {
                newPlayerMatches.add(
                        new PlayerMatch(player, match)
                );
            }
        }

        playerMatchRepository.saveAll(newPlayerMatches);

        // reload playerMatches
        List<PlayerMatch> playerMatches =
                playerMatchRepository
                        .findByPlayerAndMatch_RiotMatchIdIn(
                                player,
                                fetchedMatchIds
                        );

        // map by riot match ID
        Map<String, PlayerMatch> playerMatchesById =
                new HashMap<>();

        for (PlayerMatch playerMatch : playerMatches) {

            playerMatchesById.put(
                    playerMatch.getMatch().getRiotMatchId(),
                    playerMatch
            );
        }

        // build frontend match responses in riot's order
        List<PlayerMatchDataResponse> matchResponses =
                new ArrayList<>();

        for (String matchId : fetchedMatchIds) {

            PlayerMatch playerMatch =
                    playerMatchesById.get(matchId);

            if (playerMatch == null) {
                continue;
            }

            Match match = playerMatch.getMatch();
            MatchResponse rawData = match.getRawData();

            matchResponses.add(
                    new PlayerMatchDataResponse(
                            match.getRiotMatchId(),
                            rawData.info(),
                            playerMatch.getParticipant(),
                            match.getRecordedAt()
                    )
            );
        }

        // Build refreshed player response
        PlayerResponse playerResponse = new PlayerResponse(
                player.getGameName(),
                player.getTagLine(),
                player.getRegion()
        );

        RankSnapshot savedRankSnapshot = saveRankSnapshot(player, riotRankResponse);
        RankSnapshotResponse refreshedRank = new RankSnapshotResponse(
                savedRankSnapshot.getTier(),
                savedRankSnapshot.getRank(),
                savedRankSnapshot.getLeaguePoints(),
                savedRankSnapshot.getWins(),
                savedRankSnapshot.getLosses(),
                savedRankSnapshot.getRecordedAt()
        );


        PlayerOverviewResponse playerOverviewResponse =
                new PlayerOverviewResponse (
                        playerResponse,
                        refreshedRank
                );

        // Return everything to frontend
        return new PlayerRefreshResponse(
                playerOverviewResponse,
                matchResponses
        );

    }
}
