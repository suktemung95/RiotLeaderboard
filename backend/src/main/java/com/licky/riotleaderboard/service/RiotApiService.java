package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.MatchResponse;
import com.licky.riotleaderboard.dto.RiotAccountResponse;
import com.licky.riotleaderboard.dto.RiotRankResponse;
import com.licky.riotleaderboard.exception.InvalidRiotApiTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RiotApiService {

    private final RestClient restClient;
    public RiotApiService(@Value("${riot.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    public RiotAccountResponse getAccount(String gameName, String tagLine) {
        return riotRequest(
                "https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
                RiotAccountResponse.class,
                gameName,
                tagLine
        );
    }

    public List<RiotRankResponse> getRankedStats(String puuid, String region) {
        String platform = regionToPlatform(region);

        return riotRequest(
                "https://{platform}.api.riotgames.com/lol/league/v4/entries/by-puuid/{puuid}",
                new ParameterizedTypeReference<List<RiotRankResponse>>() {},
                platform,
                puuid
        );
    }

    public List<String> getMatchIds(String puuid) {
        return riotRequest(
                "https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/{puuid}/ids",
                new ParameterizedTypeReference<List<String>>() {},
                puuid
        );
    }

    public MatchResponse getMatchDetails(String matchId) {
        return riotRequest(
                "https://americas.api.riotgames.com/lol/match/v5/matches/{matchId}",
                MatchResponse.class,
                matchId
        );
    }

    private <T> T riotRequest(
            String uri,
            Class<T> responseType,
            Object... uriVariables
    ) {
        return restClient.get()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(
                        status -> status.value() == 401 || status.value() == 403,
                        (req, res) -> {
                            throw new InvalidRiotApiTokenException();
                        }
                ).body(responseType);
    }

    private <T> T riotRequest(
            String uri,
            ParameterizedTypeReference<T> responseType,
            Object... uriVariables
    ) {
        return restClient.get()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(
                        status -> status.value() == 401 || status.value() == 403,
                        (req, res) -> {
                            throw new InvalidRiotApiTokenException();
                        }
                ).body(responseType);
    }

    private String regionToPlatform(String region) {
        return switch (region.toUpperCase()) {
            case "NA", "NA1" -> "na1";
            case "EUW", "EUW1" -> "euw1";
            case "EUNE", "EUN1" -> "eun1";
            case "KR" -> "kr";
            case "JP", "JP1" -> "jp1";
            default -> throw new IllegalArgumentException("Unsupported region: " + region);
        };
    }

}
