package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.MatchResponse;
import com.licky.riotleaderboard.dto.RiotAccountResponse;
import com.licky.riotleaderboard.dto.RiotRankResponse;
import com.licky.riotleaderboard.exception.InvalidRiotApiTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RiotApiService {

    private final String apiKey;
    private final RestClient restClient;
    public RiotApiService(@Value("${riot.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    public RiotAccountResponse getAccount(String gameName, String tagLine) {
        return restClient.get()
                .uri("https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
                        gameName,
                        tagLine
                ).header("X-Riot-Token", apiKey)
                .retrieve()
                .body(RiotAccountResponse.class);
    }

    public List<RiotRankResponse> getRankedStats(String puuid, String region) {
        String platform = switch (region.toUpperCase()) {
            case "NA", "NA1" -> "na1";
            case "EUW", "EUW1" -> "euw1";
            case "EUNE", "EUN1" -> "eun1";
            case "KR" -> "kr";
            case "JP", "JP1" -> "jp1";
            default -> throw new IllegalArgumentException("Unsupported region: " + region);
        };

        return restClient.get()
                .uri(
                        "https://{platform}.api.riotgames.com/lol/league/v4/entries/by-puuid/{puuid}",
                        platform,
                        puuid
                )
                .header("X-Riot-Token", apiKey)
                .retrieve()
                .onStatus(
                        status -> status.value() == 401,
                        (req, res) -> {
                            throw new InvalidRiotApiTokenException();
                        }
                )
                .body(new ParameterizedTypeReference<List<RiotRankResponse>>() {});

    }

    public List<String> getMatchIds(String puuid) {
        return restClient.get()
                .uri("https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/{puuid}/ids",
                        puuid
                ).header("X-Riot-Token", apiKey)
                .retrieve()
                .onStatus(
                        status -> status.value() == 401 || status.value() == 403,
                        (req, res) -> {
                            throw new InvalidRiotApiTokenException();
                        }
                )
                .body(new ParameterizedTypeReference<List<String>>(){});
    }

    public MatchResponse getMatchDetails(String matchId) {
        return restClient.get()
                .uri("https://americas.api.riotgames.com/lol/match/v5/matches/{matchId}",
                    matchId
                ).header("X-Riot-Token", apiKey)
                .retrieve()
                .onStatus(
                        status -> status.value() == 401 || status.value() == 403,
                        (req, res) -> {
                            throw new InvalidRiotApiTokenException();
                        }
                ).body(MatchResponse.class);
    }

}
