package com.example.demo.Controller;

import com.example.demo.models.Song;
import com.example.demo.services.PlaylistManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MusicPlayerController {

    private final PlaylistManager playlistManager;

    public MusicPlayerController(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    // Liste complete
    @GetMapping("/api/songs")
    public List<Song> getSongs() {
        return playlistManager.getAllSongs();
    }

    // Chanson actuelle
    @GetMapping("/api/songs/current")
    public Song getCurrentSong() {
        return playlistManager.getCurrentSong();
    }

    // Suivante
    @GetMapping("/api/songs/next")
    public Song nextSong() {
        playlistManager.nextSong();
        return playlistManager.getCurrentSong();
    }

    // Precedente
    @GetMapping("/api/songs/prev")
    public Song prevSong() {
        playlistManager.prevSong();
        return playlistManager.getCurrentSong();
    }

    // Selection par index
    @GetMapping("/api/songs/select/{index}")
    public Song selectSong(@PathVariable int index) {
        playlistManager.selectSong(index);
        return playlistManager.getCurrentSong();
    }

    // Selection aleatoire selon le mood (avec anti-repeat)
    @GetMapping("/api/songs/mood/{mood}")
    public Song getRandomSongByMood(@PathVariable String mood) {
        return playlistManager
                .getRandomSongByMood(mood)
                .map(song -> {
                    int index = playlistManager.getAllSongs().indexOf(song);
                    if (index >= 0) playlistManager.selectSong(index);
                    return song;
                })
                .orElse(null);
    }

    // Baisser le volume de la chanson actuelle
    @GetMapping("/api/songs/volume/decrease/{amount}")
    public Song decreaseVolume(@PathVariable int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit etre positif.");
        playlistManager.decreaseVolume(amount);
        return playlistManager.getCurrentSong();
    }

    // Augmenter le volume de la chanson actuelle
    @GetMapping("/api/songs/volume/increase/{amount}")
    public Song increaseVolume(@PathVariable int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit etre positif.");
        playlistManager.increaseVolume(amount);
        return playlistManager.getCurrentSong();
    }
}
