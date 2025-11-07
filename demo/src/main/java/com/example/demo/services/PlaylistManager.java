package com.example.demo.services;

import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PlaylistManager {

    private Playlist playlist;

    public PlaylistManager() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Song>> typeReference = new TypeReference<>() {};

        try {
            // Cherche le fichier dans src/main/resources/data/songs.json
            ClassPathResource resource = new ClassPathResource("data/songs.json");
            System.out.println("Exists? " + resource.exists());
            List<Song> songs = mapper.readValue(resource.getInputStream(), typeReference);
            this.playlist = new Playlist(songs);
        } catch (IOException e) {
            e.printStackTrace();
            this.playlist = new Playlist(List.of());
        }
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

    // 🔹 Retourne toutes les chansons correspondant à un Mood
    public List<Song> getSongsByMood(String mood) {
        return playlist.getByMood(mood);
    }

    // 🔹 Sélectionne la première chanson d’un Mood spécifique
    public void selectSongByMood(String mood) {
        playlist.selectByMood(mood);
    }

    public void decreaseVolume(int amount) {
        Song current = getCurrentSong();
        int newVolume = current.getVolume() - amount;
        current.setVolume(newVolume);
        System.out.println("🔉 Volume baissé à : " + current.getVolume() + "% pour " + current.getName());
    }
}
