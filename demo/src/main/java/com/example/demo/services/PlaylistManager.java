package com.example.demo.services;

import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import com.example.demo.repo.SongRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PlaylistManager compatible avec la branche luca,
 * mais basé sur la repo et RecommendationService de la refonte.
 */
@Service
public class PlaylistManager {

    private final SongRepository repo;
    private final RecommendationService recommendationService;
    private final Playlist playlist;

    public PlaylistManager(SongRepository repo, RecommendationService recommendationService) {
        this.repo = repo;
        this.recommendationService = recommendationService;
        this.playlist = new Playlist(repo.findAll());
    }

    public List<Song> getAllSongs() {
        return playlist.getSongs();
    }

    public Song getCurrentSong() {
        return playlist.getCurrentSong();
    }

    public void nextSong() {
        playlist.next();
    }

    public void prevSong() {
        playlist.prev();
    }

    public void selectSong(int index) {
        playlist.select(index);
    }

    public List<Song> getSongsByMood(String mood) {
        return playlist.getByMood(mood);
    }

    public void selectSongByMood(String mood) {
        playlist.selectByMood(mood);
    }

    public Optional<Song> getRandomSongByMood(String mood) {
        if (mood == null || mood.isBlank()) return Optional.empty();
        List<Song> filtered = repo.findAll().stream()
                .filter(s -> s.hasMood(mood))
                .collect(Collectors.toList());
        return recommendationService.pickNext(filtered);
    }

    public void decreaseVolume(int amount) {
        Song current = getCurrentSong();
        if (current == null) return;
        current.setVolume(current.getVolume() - amount);
    }

    public void increaseVolume(int amount) {
        Song current = getCurrentSong();
        if (current == null) return;
        current.setVolume(current.getVolume() + amount);
    }
}
