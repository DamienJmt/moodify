package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Song model.
 * Backward compatible with the original luca JSON:
 *   { "name", "artists", "link", "image", "mood" }
 * New refonte fields are supported:
 *   { "id", "title", "artist", "album", "durationSec", "moods", "metadata" }
 */
public class Song {

    // ----- New fields -----
    private String id;

    @JsonAlias({"title"})
    private String name;       // kept as "name" for luca front

    @JsonAlias({"artist"})
    private String artists;    // luca expects "artists" string

    @JsonAlias({"pathOrUrl"})
    private String link;

    private String image;

    private String album;

    private int durationSec;

    private Set<String> moods = new LinkedHashSet<>();

    private Map<String, String> metadata = new HashMap<>();

    // luca field (single mood) still accepted
    @JsonProperty("mood")
    private String mood;

    private int volume = 100;

    public Song() {}

    public Song(String name, String artists, String link, String image, String mood) {
        this.name = name;
        this.artists = artists;
        this.link = link;
        this.image = image;
        this.mood = mood;
        if (mood != null && !mood.isBlank()) {
            this.moods.add(mood);
        }
    }

    // ----- getters/setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArtists() { return artists; }
    public void setArtists(String artists) { this.artists = artists; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }

    public Set<String> getMoods() { return moods; }
    public void setMoods(Set<String> moods) {
        this.moods = (moods == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(moods);
    }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = (metadata == null) ? new HashMap<>() : new HashMap<>(metadata);
    }

    public String getMood() { return mood; }
    public void setMood(String mood) {
        this.mood = mood;
        if (mood != null && !mood.isBlank()) {
            this.moods.add(mood);
        }
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
    }

    @JsonIgnore
    public boolean hasMood(String mood) {
        if (mood == null) return false;
        if (this.mood != null && this.mood.equalsIgnoreCase(mood)) return true;
        return moods.stream().anyMatch(m -> m.equalsIgnoreCase(mood));
    }

    /** Ensure a single mood is set for luca front. */
    @JsonIgnore
    public void normalizeSingleMood() {
        if ((mood == null || mood.isBlank()) && !moods.isEmpty()) {
            mood = moods.iterator().next();
        }
        if (mood != null && !mood.isBlank()) {
            moods.add(mood);
        }
    }
}
