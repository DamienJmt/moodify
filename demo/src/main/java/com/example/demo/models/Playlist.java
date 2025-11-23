package com.example.demo.models;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Playlist enrichie (refonte) + compat luca.
 */
public class Playlist {
    private String id;
    private String name;
    private String description;
    private final List<Song> songs = new ArrayList<>();
    private int currentIndex = -1;

    public Playlist() {}

    // constructeur luca historique
    public Playlist(List<Song> songs) {
        if (songs != null) this.songs.addAll(songs);
        if (!this.songs.isEmpty()) currentIndex = 0;
    }

    public Playlist(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Song> getSongs() { return Collections.unmodifiableList(songs); }

    public void addSong(Song s) {
        if (s == null) return;
        songs.add(s);
        if (currentIndex == -1) currentIndex = 0;
    }

    public void removeSong(Song s) {
        songs.remove(s);
        if (songs.isEmpty()) currentIndex = -1;
        else currentIndex = Math.min(currentIndex, songs.size() - 1);
    }

    public Song getCurrentSong() {
        if (songs.isEmpty() || currentIndex < 0) return null;
        return songs.get(currentIndex);
    }

    public Optional<Song> getCurrentSongOpt() {
        return Optional.ofNullable(getCurrentSong());
    }

    public void next() {
        if (!songs.isEmpty()) currentIndex = (currentIndex + 1) % songs.size();
    }

    public void prev() {
        if (!songs.isEmpty()) currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
    }

    public void select(int index) {
        if (index >= 0 && index < songs.size()) currentIndex = index;
    }

    // ---- Mood helpers (compat luca) ----

    public List<Song> getByMood(String mood) {
        if (mood == null) return List.of();
        return songs.stream().filter(s -> s.hasMood(mood)).collect(Collectors.toList());
    }

    public void selectByMood(String mood) {
        List<Song> filtered = getByMood(mood);
        if (!filtered.isEmpty()) currentIndex = songs.indexOf(filtered.get(0));
    }
}
