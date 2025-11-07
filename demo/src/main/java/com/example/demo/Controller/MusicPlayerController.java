package com.example.demo.Controller;

import com.example.demo.models.Song;
import com.example.demo.services.PlaylistManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
public class MusicPlayerController {

    private final PlaylistManager playlistManager;
    private final Random random = new Random();

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
        playlistManager.nextSong();
        return playlistManager.getCurrentSong();
    }

    // ⏮️ Précédente
    @GetMapping("/api/songs/prev")
    public Song prevSong() {
        playlistManager.prevSong();
        return playlistManager.getCurrentSong();
    }

    // 🔢 Sélection par index
    @GetMapping("/api/songs/select/{index}")
    public Song selectSong(@PathVariable int index) {
        playlistManager.selectSong(index);
        return playlistManager.getCurrentSong();
    }

    // 💫 Sélection aléatoire selon le mood
    @GetMapping("/api/songs/mood/{mood}")
    public Song getRandomSongByMood(@PathVariable String mood) {
        if (mood == null || mood.isBlank()) {
            return null; // protection contre les requêtes vides
        }

        List<Song> filtered = playlistManager.getAllSongs().stream()
                .filter(s -> s.getMood() != null && s.getMood().equalsIgnoreCase(mood))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("⚠️ Aucune chanson trouvée pour le mood : " + mood);
            return null;
        }

        Song selected = filtered.get(random.nextInt(filtered.size()));

        // facultatif : met à jour la chanson actuelle
        int index = playlistManager.getAllSongs().indexOf(selected);
        if (index >= 0) {
            playlistManager.selectSong(index);
        }

        System.out.println("🎶 Chanson aléatoire sélectionnée : " + selected.getName() + " (" + selected.getMood() + ")");
        return selected;
    }

    // 🔉 Baisser le volume de la chanson actuelle
    @GetMapping("/api/songs/volume/decrease/{amount}")
    public Song decreaseVolume(@PathVariable int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }

        playlistManager.decreaseVolume(amount);
        return playlistManager.getCurrentSong();
    }

    // 🔊 Augmenter le volume de la chanson actuelle
    @GetMapping("/api/songs/volume/increase/{amount}")
    public Song increaseVolume(@PathVariable int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }

        Song current = playlistManager.getCurrentSong();
        current.setVolume(current.getVolume() + amount);
        return current;
    }

}
