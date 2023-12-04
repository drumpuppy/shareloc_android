package com.example.shareloc;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String nickname;
    private Map<String, Boolean> countriesVisited;
    private Map<String, Boolean> achievements;

    private List<String> friends;

    public User() {
        this.username = "";
        this.nickname = "";
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
        this.friends = new ArrayList<>();
    }
    public User(String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
    }

    private Map<String, Boolean> createDefaultAchievements() {
        Map<String, Boolean> achievements = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            achievements.put("achievement_" + i, false);
        }
        return achievements;
    }

    private Map<String, Boolean> createDefaultCountries() {
        Map<String, Boolean> countries = new HashMap<>();

        countries.put("albania", false);
        countries.put("austria", false);
        countries.put("belarus", false);
        countries.put("belgium", false);
        countries.put("bosnia-and-herzegovina", false);
        countries.put("bulgaria", false);
        countries.put("croatia", false);
        countries.put("cyprus", false);
        countries.put("czech-republic", false);
        countries.put("denmark", false);
        countries.put("estonia", false);
        countries.put("finland", false);
        countries.put("france", false);
        countries.put("germany", false);
        countries.put("greece", false);
        countries.put("hungary", false);
        countries.put("ireland", false);
        countries.put("italy", false);
        countries.put("latvia", false);
        countries.put("lithuania", false);
        countries.put("luxembourg", false);
        countries.put("malta", false);
        countries.put("moldova", false);
        countries.put("montenegro", false);
        countries.put("netherlands", false);
        countries.put("norway", false);
        countries.put("poland", false);
        countries.put("portugal", false);
        countries.put("republic-of-north-macedonia", false);
        countries.put("romania", false);
        countries.put("serbia", false);
        countries.put("slovakia", false);
        countries.put("slovenia", false);
        countries.put("spain", false);
        countries.put("sweden", false);
        countries.put("switzerland", false);
        countries.put("ukraine", false);
        countries.put("united-kingdom", false);

        return countries;
    }


    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public Map<String, Boolean> getCountriesVisited() {
        return countriesVisited;
    }

    public Map<String, Boolean> getAchievements() {
        return achievements;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCountriesVisited(Map<String, Boolean> countriesVisited) {
        this.countriesVisited = countriesVisited;
    }

    public void setAchievements(Map<String, Boolean> achievements) {
        this.achievements = achievements;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
