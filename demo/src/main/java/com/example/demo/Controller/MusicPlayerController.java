package com.example.demo.Controller;

import com.example.demo.models.Song;
import com.example.demo.services.PlaylistManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MusicPlayerController {

    private final PlaylistManager playlistManager;

    public MusicPlayerController(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    // 🎵 Liste complète
    @GetMapping("/api/songs")
    public List<Song> getSongs() {
        return playlistManager.getAllSongs();
    }

    // 🎧 Chanson actuelle
    @GetMapping("/api/songs/current")
    public Song getCurrentSong() {
        return playlistManager.getCurrentSong();
    }

    // ⏭️ Suivante
    @GetMapping("/api/songs/next")
    public Song nextSong() {
        return playlistManager.nextSong();
    }

    // ⏮️ Précédente
    @GetMapping("/api/songs/prev")
    public Song prevSong() {
        return playlistManager.prevSong();
    }

    // ▶️ Choisir une chanson par index
    @GetMapping("/api/songs/select/{index}")
    public Song selectSong(@PathVariable int index) {
        playlistManager.selectSong(index);
        return playlistManager.getCurrentSong();
    }

    // 💫 Sélection aléatoire selon le mood (anti-repeat)
    @GetMapping("/api/songs/mood/{mood}")
    public Song getRandomSongByMood(@PathVariable String mood) {
        if (mood == null || mood.isBlank()) return null;
        Optional<Song> pick = playlistManager.getRandomSongByMood(mood);
        pick.ifPresent(s -> playlistManager.selectSong(playlistManager.getAllSongs().indexOf(s)));
        return pick.orElse(null);
    }

    // 🔉 Baisser le volume
    @GetMapping("/api/songs/volume/decrease/{amount}")
    public Song decreaseVolume(@PathVariable int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit être positif.");
        playlistManager.decreaseVolume(amount);
        return playlistManager.getCurrentSong();
    }

    // 🔊 Augmenter le volume
    @GetMapping("/api/songs/volume/increase/{amount}")
    public Song increaseVolume(@PathVariable int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit être positif.");
        playlistManager.increaseVolume(amount);
        return playlistManager.getCurrentSong();
    }
}
