package com.licky.riotleaderboard.controller;

import com.licky.riotleaderboard.dto.*;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.service.PlayerService;
import com.licky.riotleaderboard.util.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Player>> addPlayer(@RequestBody PlayerResponse request) {
        AddPlayerResult apr = playerService.addPlayer(request);

        if (apr.alreadyExists()) {
            return ApiResponses.build(
                    HttpStatus.OK,
                    true,
                    "Player already exists",
                    apr.player()
            );
        }
        return ApiResponses.build(
                HttpStatus.CREATED,
                true,
                "Added player",
                apr.player()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlayerRankResponse>>> getPlayers() {
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Retrieved all players",
                playerService.getLeaderboard()
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<RankSnapshotResponse>>> getPlayerHistory(
            @ModelAttribute PlayerRequest playerRequest
    ) {
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Retrieved player history",
                playerService.getPlayerHistory(playerRequest)
        );
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<PlayerOverviewResponse>> getPlayerOverview(
            @ModelAttribute PlayerRequest playerRequest
    ) {
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Player overview retrieved",
                playerService.getPlayerOverview(playerRequest)
        );
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<PaginatedResponse<PlayerMatchDataResponse>>> getPlayerMatches(
            @ModelAttribute PlayerRequest playerRequest,
            @RequestParam(required = false) Long before
    ) {

        List<PlayerMatchDataResponse> matches = playerService.getPlayerMatches(playerRequest, before);
        boolean hasNext = matches.size() > 20;

        List<PlayerMatchDataResponse> returnedMatches =
                hasNext
                        ? matches.subList(0, 20)
                        : matches;
        
        Long nextCursor = returnedMatches.isEmpty()
                ? null
                : returnedMatches.getLast().info().gameStartTimestamp();
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Player matches returned successfully",
                new PaginatedResponse<>(
                        returnedMatches, nextCursor, hasNext
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<PlayerRefreshResponse>> refreshPlayer(
            @ModelAttribute PlayerRequest playerRequest) {
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Player refreshed successfully",
                playerService.refreshPlayerData(playerRequest)
        );
    }
}