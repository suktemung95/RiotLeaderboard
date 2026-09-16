package com.licky.riotleaderboard.service;

import com.licky.riotleaderboard.dto.RiotAccountResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}
