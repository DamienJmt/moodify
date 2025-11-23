package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {
    private String id;

    // "name" (luca) ou "title" (refonte)
    @NotBlank
    @JsonAlias({"name", "title"})
    private String title;

    @JsonAlias({"artists", "artist"})
    private String artist;

    private String album;

    // "link" (luca) ou "pathOrUrl" (refonte)
    @JsonAlias({"link", "pathOrUrl"})
    private String link;

    private String image;

    @Positive
    private int durationSec;

    // Une chanson peut avoir plusieurs humeurs
    private Set<String> moods = new LinkedHashSet<>();

    private Map<String, String> metadata = new HashMap<>();

    private int volume = 100; // volume par défaut côté serveur

    public Song() {}

    public Song(String id, String title, String artist, String album, String link, String image,
                int durationSec, Collection<String> moods) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.link = link;
        this.image = image;
        this.durationSec = durationSec;
        if (moods != null) this.moods.addAll(moods);
    }

    // JSON luca: mood: "Happy" (string)
    @JsonSetter("mood")
    public void setMoodSingle(String mood) {
        if (mood != null && !mood.isBlank()) this.moods.add(mood);
    }

    // JSON refonte: moods: ["Happy", "Chill"]
    @JsonSetter("moods")
    public void setMoods(Set<String> moods) {
        if (moods != null) this.moods = new LinkedHashSet<>(moods);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // backward compat: getName() / setName()
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    @JsonGetter("name")
    public String getName() { return title; }
    @JsonSetter("name")
    public void setName(String name) { this.title = name; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    @JsonGetter("artists")
    public String getArtists() { return artist; }
    @JsonSetter("artists")
    public void setArtists(String artists) { this.artist = artists; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }

    @JsonProperty("moods")
    public Set<String> getMoods() { return moods; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * champ "mood" historique : on renvoie la première humeur (ou null).
     */
    @JsonProperty("mood")
    public String getMood() {
        return moods.stream().findFirst().orElse(null);
    }

    /** remplace toutes les moods par une seule (compat). */
    public void setMood(String mood) {
        this.moods.clear();
        if (mood != null && !mood.isBlank()) this.moods.add(mood);
    }

    public boolean hasMood(String mood) {
        return mood != null && moods.stream().anyMatch(m -> m.equalsIgnoreCase(mood));
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
    }

    @Override public String toString() {
        return "Song{" + title + " by " + artist + "}";
    }
}
