package com.moodify.model;

import java.util.*;

public class Playlist {
    private String id;
    private String name;
    private String description;
    private final List<Song> songs = new ArrayList<>();
    private int currentIndex = -1;

    public Playlist() {}

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

    public void addSong(Song s) { if (s != null) songs.add(s); if (currentIndex == -1) currentIndex = 0; }
    public void removeSong(Song s) { songs.remove(s); if (songs.isEmpty()) currentIndex = -1; else currentIndex = Math.min(currentIndex, songs.size()-1); }
    public void clear() { songs.clear(); currentIndex = -1; }

    public Optional<Song> next() {
        if (songs.isEmpty()) return Optional.empty();
        currentIndex = (currentIndex + 1) % songs.size();
        return Optional.of(songs.get(currentIndex));
    }
    public Optional<Song> previous() {
        if (songs.isEmpty()) return Optional.empty();
        currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
        return Optional.of(songs.get(currentIndex));
    }
    public Optional<Song> getCurrentSong() {
        if (songs.isEmpty() || currentIndex < 0) return Optional.empty();
        return Optional.of(songs.get(currentIndex));
    }
}
