package com.example.shareloc;

import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private List<String> countriesVisited;
    private Map<String, Boolean> achievements;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    // Getters
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

    // Setters
    public void setUsername(String username) {
        this.username = username;
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
