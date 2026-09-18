package com.licky.riotleaderboard.controller;

import com.licky.riotleaderboard.dto.AddPlayerRequest;
import com.licky.riotleaderboard.dto.PlayerResponse;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public Player addPlayer(@RequestBody AddPlayerRequest request) {
        return playerService.addPlayer(request);
    }

    @GetMapping
    public List<PlayerResponse> getPlayers() {
        return playerService.getLeaderboard();
    }
}
