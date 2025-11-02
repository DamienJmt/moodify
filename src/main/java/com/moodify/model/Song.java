package com.moodify.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.*;

public class Song {
    private String id;

    @NotBlank
    private String title;

    private String artist;
    private String album;
    private String pathOrUrl;

    @Positive
    private int durationSec;

    private Set<String> moods = new LinkedHashSet<>(); // flexible, définit par l'utilisateur

    private Map<String, String> metadata = new HashMap<>(); // exemple: genre, année, bpm

    public Song() {}

    public Song(String id, String title, String artist, String album, String pathOrUrl, int durationSec, Collection<String> moods) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.pathOrUrl = pathOrUrl;
        this.durationSec = durationSec;
        if (moods != null) this.moods.addAll(moods);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getPathOrUrl() { return pathOrUrl; }
    public void setPathOrUrl(String pathOrUrl) { this.pathOrUrl = pathOrUrl; }
    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public Set<String> getMoods() { return moods; }
    public void setMoods(Set<String> moods) { this.moods = moods; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public boolean hasMood(String mood) {
        return moods.stream().anyMatch(m -> m.equalsIgnoreCase(mood));
    }

    @Override public String toString() {
        return "Song{" + title + " by " + artist + "}";
    }
}
