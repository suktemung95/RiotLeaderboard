package com.licky.riotleaderboard.controller;

import com.licky.riotleaderboard.dto.AddPlayerRequest;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.service.PlayerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
