package com.licky.riotleaderboard.background;

import com.licky.riotleaderboard.dto.PlayerRequest;
import com.licky.riotleaderboard.model.Player;
import com.licky.riotleaderboard.repository.PlayerRepository;
import com.licky.riotleaderboard.service.PlayerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerRefreshScheduler {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    public PlayerRefreshScheduler(
            PlayerService playerService,
            PlayerRepository playerRepository
    ) {
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    @Scheduled(fixedRate = 900000)
    public void refreshAll() {
        List<Player> players = playerRepository.findAll();
        List<String> successfulPlayersRefreshed = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Player player : players) {

            try {
                playerService.refreshPlayerData(new PlayerRequest(player));
                successfulPlayersRefreshed.add(
                        player.getRegion() + ": " + player.getGameName() + "#" + player.getTagLine());
            } catch (Exception e) {
                errors.add(
                        player.getGameName() + "#" + player.getTagLine()
                                + ": " + e.getMessage()
                );
            }
        }
        System.out.println("Succesfully refreshed: " + successfulPlayersRefreshed);
        System.out.println("Errors when refreshing: " + errors);
    }
}
