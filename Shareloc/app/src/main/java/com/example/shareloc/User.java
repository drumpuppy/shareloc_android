package com.example.shareloc;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private Map<String, Boolean> countriesVisited;
    private Map<String, Boolean> achievements;

    public User() {
        this.username = "";
        this.nickname = "";
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
    }
    public User(String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
    }

    private Map<String, Boolean> createDefaultAchievements() {
        Map<String, Boolean> achievements = new HashMap<>();
        achievements.put("Mon petit poney : trouve ton premier pays", false);
        achievements.put("Randoneur: découvre 5 pays", false);
        achievements.put("Expert: trouve 40 pays", false);
        achievements.put("God Save the Queen: trouve tous les pays du CommonWealth", false);
        achievements.put("Mister WorldWide: découvre le monde entier !", false);
        achievements.put("I go solo: fait toi un ami",false);
        achievements.put("Où es-tu ?: découvre la carte d'un(e) ami(e)", false);
        achievements.put("Stalker: découvre la carte de 5 amis", false);
        achievements.put("Holy Moly: va au Vatican", false);

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
}
