package com.example.demo.services;

import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import com.example.demo.repo.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistManager {

    private final Playlist playlist;
    private final RecommendationService recommendationService;

    public PlaylistManager(SongRepository songRepository,
                           RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
        List<Song> songs = songRepository.findAll();
        this.playlist = new Playlist(songs);
    }

    public List<Song> getAllSongs() {
        return playlist.getSongs();
    }

    public Song getCurrentSong() {
        return playlist.getCurrentSong();
    }

    public Song nextSong() {
        return playlist.nextSong();
    }

    public Song prevSong() {
        return playlist.prevSong();
    }

    public void selectSong(int index) {
        playlist.selectSong(index);
    }

    public List<Song> getSongsByMood(String mood) {
        return playlist.getByMood(mood);
    }

    public void selectSongByMood(String mood) {
        playlist.selectByMood(mood);
    }

    public Optional<Song> getRandomSongByMood(String mood) {
        return recommendationService.pickNext(getSongsByMood(mood));
    }

    public void increaseVolume(int amount) {
        Song current = getCurrentSong();
        if (current == null) return;
        current.setVolume(current.getVolume() + amount);
    }

    public void decreaseVolume(int amount) {
        Song current = getCurrentSong();
        if (current == null) return;
        current.setVolume(current.getVolume() - amount);
    }
}
