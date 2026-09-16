package com.licky.riotleaderboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gameName;
    private String tagLine;
    private String region;
    private String puuid;

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getRegion() {
        return region;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    public String getPuuid() {
        return puuid;
    }
}
