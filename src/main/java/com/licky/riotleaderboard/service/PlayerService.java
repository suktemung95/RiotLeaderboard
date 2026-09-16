package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.AddPlayerRequest;
import com.licky.riotleaderboard.dto.RiotAccountResponse;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final RiotApiService riotApiService;

    public PlayerService(
            PlayerRepository playerRepository,
            RiotApiService riotApiService
    ) {
        this.playerRepository = playerRepository;
        this.riotApiService = riotApiService;
    }

    public Player addPlayer(AddPlayerRequest request) {
        RiotAccountResponse riotAccount =
                riotApiService.getAccount(request.gameName(), request.tagLine());

        Player player = new Player();

        player.setGameName(riotAccount.gameName());
        player.setTagLine(riotAccount.tagLine());
        player.setRegion(request.region());
        player.setPuuid(riotAccount.puuid());

        return playerRepository.save(player);
    }
}
