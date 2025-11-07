package com.example.demo.models;

import java.util.List;
import java.util.stream.Collectors;

public class Playlist {
    private List<Song> songs;
    private int currentIndex = 0;

    public Playlist(List<Song> songs) {
        this.songs = songs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public Song getCurrentSong() {
        if (songs.isEmpty()) return null;
        return songs.get(currentIndex);
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

    // 🔹 Retourne les chansons filtrées par Mood
    public List<Song> getByMood(String mood) {
        return songs.stream()
                .filter(song -> song.getMood().equalsIgnoreCase(mood))
                .collect(Collectors.toList());
    }

    // 🔹 Sélectionne la première chanson d’un Mood spécifique
    public void selectByMood(String mood) {
        List<Song> filtered = getByMood(mood);
        if (!filtered.isEmpty()) {
            currentIndex = songs.indexOf(filtered.get(0));
        }
    }
}
