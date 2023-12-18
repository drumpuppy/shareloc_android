package com.example.shareloc.Class;

import android.location.Location;

import com.example.shareloc.managers.GeoJsonManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userId;
    private String username;
    private String nickname;
    private Map<String, Boolean> countriesVisited;
    private Map<String, Boolean> mapVisited;
    private Map<String, Boolean> achievements;
    private List<Location> positions_found;
    private UserLocation lastUpdatedPosition;
    public User() {
        this.username = "";
        this.nickname = "";
        this.countriesVisited = GeoJsonManager.createDefaultCountries();
        this.achievements = createDefaultAchievements();
        this.positions_found = createDefaultPostionFound();
        this.lastUpdatedPosition = new UserLocation();
        mapVisited = new HashMap<>();

    }
    public User(String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.countriesVisited = GeoJsonManager.createDefaultCountries();
        this.achievements = createDefaultAchievements();
        this.positions_found = createDefaultPostionFound();
        this.lastUpdatedPosition = new UserLocation();
        mapVisited = new HashMap<>();
    }

    public Map<String, Boolean> getMapVisited() {
        return mapVisited;
    }

    private List<Location> createDefaultPostionFound(){
        return new ArrayList<>();
    }

    private Map<String, Boolean> createDefaultAchievements() {
        Map<String, Boolean> achievements = new HashMap<>();
        achievements.put("Mon petit poney : trouve ton premier pays", false);
        achievements.put("Randoneur: découvre 5 pays", false);
        achievements.put("Expert: trouve 40 pays", false);
        achievements.put("God Save the Queen: trouve tous les pays du CommonWealth", false);
        achievements.put("Mister WorldWide: découvre le monde entier !", false);
        achievements.put("I go solo: fais toi un ami",false);
        achievements.put("Où es-tu ?: découvre la carte d'un(e) ami(e)", false);
        achievements.put("Stalker: découvre la carte de 5 amis", false);
        achievements.put("Holy Moly: va au Vatican", false);

        return achievements;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
