package com.licky.riotleaderboard.controller;

import com.licky.riotleaderboard.dto.PlayerResponse;
import com.licky.riotleaderboard.dto.ApiResponse;
import com.licky.riotleaderboard.dto.PlayerRankResponse;
import com.licky.riotleaderboard.dto.RankSnapshotResponse;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.service.PlayerService;
import com.licky.riotleaderboard.util.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponses.build(
                HttpStatus.OK,
                "success",
                "Added player",
                playerService.addPlayer(request)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlayerRankResponse>>> getPlayers() {
        return ApiResponses.build(
                HttpStatus.OK,
                "success",
                "Retrieved all players",
                playerService.getLeaderboard()
        );
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PlayerResponse>> searchPlayer(
            @RequestParam String region,
            @RequestParam String gameName,
            @RequestParam String tagLine
    ) {
        return ApiResponses.build(
                HttpStatus.OK,
                "success",
                "retrieved player",
                playerService.getPlayerByName(region, gameName, tagLine)
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<RankSnapshotResponse>>> getPlayerHistory(@PathVariable Long id) {
        return ApiResponses.build(
                HttpStatus.OK,
                "success",
                "Retrieved player history",
                playerService.getPlayerHistory(id)
        );
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<ApiResponse<RankSnapshotResponse>> refreshPlayer(@PathVariable Long id) {
        return ApiResponses.build(
                HttpStatus.OK,
                "success",
                "Refreshed player rank snapshot",
                playerService.refreshPlayer(id)
        );
    }
}
