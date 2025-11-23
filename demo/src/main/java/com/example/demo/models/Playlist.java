package com.example.demo.models;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Playback playlist + custom playlists (refonte).
 * Keeps luca public API for compatibility.
 */
public class Playlist {
    private String id;
    private String name;
    private String description;

    private final List<Song> songs = new ArrayList<>();
    private int currentIndex = 0;

    public Playlist() {}

    // luca constructor
    public Playlist(List<Song> songs) {
        if (songs != null) this.songs.addAll(songs);
        if (!this.songs.isEmpty()) currentIndex = 0;
    }

    // refonte-style constructor
    public Playlist(String id, String name, String description, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (songs != null) this.songs.addAll(songs);
        if (!this.songs.isEmpty()) currentIndex = 0;
    }

    public List<Song> getSongs() { return songs; }

    public Song getCurrentSong() {
        if (songs.isEmpty()) return null;
        currentIndex = Math.max(0, Math.min(currentIndex, songs.size() - 1));
        return songs.get(currentIndex);
    }

    public Song nextSong() {
        if (songs.isEmpty()) return null;
        currentIndex = (currentIndex + 1) % songs.size();
        return songs.get(currentIndex);
    }

    public Song prevSong() {
        if (songs.isEmpty()) return null;
        currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
        return songs.get(currentIndex);
    }

    public void selectSong(int index) {
        if (songs.isEmpty()) return;
        currentIndex = Math.max(0, Math.min(index, songs.size() - 1));
    }

    public List<Song> getByMood(String mood) {
        return songs.stream()
                .filter(song -> song != null && song.hasMood(mood))
                .collect(Collectors.toList());
    }

    public void selectByMood(String mood) {
        List<Song> filtered = getByMood(mood);
        if (!filtered.isEmpty()) {
            currentIndex = songs.indexOf(filtered.get(0));
        }
    }

    // ----- refonte fields getters/setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
