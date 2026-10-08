package com.licky.riotleaderboard.controller;

import com.licky.riotleaderboard.dto.ApiResponse;
import com.licky.riotleaderboard.dto.MatchResponse;
import com.licky.riotleaderboard.model.Match;
import com.licky.riotleaderboard.service.MatchService;
import com.licky.riotleaderboard.util.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<ApiResponse<MatchResponse>> getMatch(@PathVariable String matchId) {
        return ApiResponses.build(
                HttpStatus.OK,
                true,
                "Match with match id " + matchId + " returned successfully",
                matchService.getMatch(matchId)
        );
    }
}
