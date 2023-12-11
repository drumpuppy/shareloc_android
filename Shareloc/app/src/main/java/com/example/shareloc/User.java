package com.example.shareloc;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private Map<String, Boolean> countriesVisited;
    private Map<String, Boolean> achievements;
    private List<Location> positions_found;

    public User() {
        this.username = "";
        this.nickname = "";
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
        this.positions_found = createDefaultPostionFound();
    }
    public User(String username,String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.countriesVisited = createDefaultCountries();
        this.achievements = createDefaultAchievements();
        this.positions_found = createDefaultPostionFound();
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

    private Map<String, Boolean> createDefaultCountries() {
        Map<String, Boolean> countries = new HashMap<>();

        // europe
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
        countries.put("russia", false);
        countries.put("iceland", false);

        //africa
        countries.put("algeria", false);
        countries.put("angola", false);
        countries.put("benin", false);
        countries.put("botswana", false);
        countries.put("burkina-faso", false);
        countries.put("burundi", false);
        countries.put("cameroon", false);
        countries.put("chad", false);
        countries.put("congo", false);
        countries.put("central-african-republic", false);
        countries.put("cote-divoire", false);
        countries.put("djibouti", false);
        countries.put("egypt", false);
        countries.put("equatorial", false);
        countries.put("eritrea", false);
        countries.put("ethiopia", false);
        countries.put("gabon", false);
        countries.put("gambia", false);
        countries.put("ghana", false);
        countries.put("guinea-bissau", false);
        countries.put("guinea", false);
        countries.put("kenya", false);
        countries.put("lesotho", false);
        countries.put("liberia", false);
        countries.put("libyan-arab-jamahiriya", false);
        countries.put("madagascar", false);
        countries.put("malawi", false);
        countries.put("mali", false);
        countries.put("mauritania", false);
        countries.put("morocco", false);
        countries.put("mozambique", false);
        countries.put("namibia", false);
        countries.put("niger", false);
        countries.put("nigeria", false);
        countries.put("rwanda", false);
        countries.put("senegal", false);
        countries.put("sierra-leone", false);
        countries.put("somalia", false);
        countries.put("south-africa", false);
        countries.put("south-sudan", false);
        countries.put("sudan", false);
        countries.put("swaziland", false);
        countries.put("tanzania", false);
        countries.put("togo", false);
        countries.put("tunisia", false);
        countries.put("uganda", false);
        countries.put("western-sahara", false);
        countries.put("zambia", false);
        countries.put("zimbabwe", false);
        countries.put("democratic-republic-of-the-congo", false);

        // America
        countries.put("argentina", false);
        countries.put("bolivia", false);
        countries.put("brazil", false);
        countries.put("canada", false);
        countries.put("chile", false);
        countries.put("colombia", false);
        countries.put("costa-rica", false);
        countries.put("cuba", false);
        countries.put("ecuador", false);
        countries.put("el-salvador", false);
        countries.put("greenland", false);
        countries.put("guatemala", false);
        countries.put("guyana", false);
        countries.put("haiti", false);
        countries.put("honduras", false);
        countries.put("jamaica", false);
        countries.put("mexico", false);
        countries.put("nicaragua", false);
        countries.put("panama", false);
        countries.put("paraguay", false);
        countries.put("peru", false);
        countries.put("puerto-rico", false);
        countries.put("suriname", false);
        countries.put("united-states", false);
        countries.put("uruguay", false);
        countries.put("venezuela", false);

        // Asia
        countries.put("afghanistan", false);
        countries.put("armenia", false);
        countries.put("azerbaijan", false);
        countries.put("bangladesh", false);
        countries.put("bhutan", false);
        countries.put("brunei-darussalam", false);
        countries.put("cambodia", false);
        countries.put("china", false);
        countries.put("democratic-peoples-republic-of-korea", false);
        countries.put("georgia", false);
        countries.put("india", false);
        countries.put("indonesia", false);
        countries.put("iran", false);
        countries.put("iraq", false);
        countries.put("israel", false);
        countries.put("japan", false);
        countries.put("jordan", false);
        countries.put("kazakhstan", false);
        countries.put("kuwait", false);
        countries.put("kyrgyzstan", false);
        countries.put("lao-peoples-democratic-republic", false);
        countries.put("lebanon", false);
        countries.put("malaysia", false);
        countries.put("mongolia", false);
        countries.put("myanmar", false);
        countries.put("nepal", false);
        countries.put("oman", false);
        countries.put("pakistan", false);
        countries.put("palestinian", false);
        countries.put("philippines", false);
        countries.put("qatar", false);
        countries.put("saudi-arabia", false);
        countries.put("south-korea", false);
        countries.put("sri-lanka", false);
        countries.put("syrian-arab-republic", false);
        countries.put("taiwan", false);
        countries.put("tajikistan", false);
        countries.put("thailand", false);
        countries.put("turkey", false);
        countries.put("turkmenistan", false);
        countries.put("united-arab-emirates", false);
        countries.put("uzbekistan", false);
        countries.put("viet-nam", false);
        countries.put("yemen", false);

        // Oceanie
        countries.put("australia", false);
        countries.put("new-zealand", false);
        countries.put("vanuatu", false);
        countries.put("fiji", false);
        countries.put("papua-new-guinea", false);
        countries.put("new-caledonia", false);
        countries.put("solomon-islands", false);


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
