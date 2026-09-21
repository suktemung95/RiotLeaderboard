package com.licky.riotleaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiotLeaderboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiotLeaderboardApplication.class, args);
    }

}
