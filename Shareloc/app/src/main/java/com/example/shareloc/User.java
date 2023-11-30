package com.example.shareloc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private List<String> countriesVisited;
    private Map<String, Boolean> achievements;

    public User() {
        this.username = "";
        this.nickname = "";
        this.countriesVisited = new ArrayList<>();
        this.achievements = createDefaultAchievements();
    }
    public User(String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.countriesVisited = new ArrayList<>();
        this.achievements = createDefaultAchievements();
    }

    private Map<String, Boolean> createDefaultAchievements() {
        Map<String, Boolean> achievements = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            achievements.put("achievement_" + i, false); // All achievements are initially false (not unlocked)
        }
        return achievements;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public List<String> getCountriesVisited() {
        return countriesVisited;
    }

    public Map<String, Boolean> getAchievements() {
        return achievements;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCountriesVisited(List<String> countriesVisited) {
        this.countriesVisited = countriesVisited;
    }

    public void setAchievements(Map<String, Boolean> achievements) {
        this.achievements = achievements;
    }
}
