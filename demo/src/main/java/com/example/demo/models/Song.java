package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Song {
    private String name;
    private String artists;
    private String link;   // chemin du fichier audio
    private String image;  // chemin de l'image
    private int volume = 100; // Volume par défaut à 100 %

    @JsonProperty("mood") // correspond à "Mood" dans le JSON
    private String mood;   // humeur de la chanson

    public Song() {}

    public Song(String name, String artists, String link, String image, String mood) {
        this.name = name;
        this.artists = artists;
        this.link = link;
        this.image = image;
        this.mood = mood;
    }

    // Getters et setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArtists() { return artists; }
    public void setArtists(String artists) { this.artists = artists; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume)); // borné entre 0 et 100
    }
}
