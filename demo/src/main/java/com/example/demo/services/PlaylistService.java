package com.example.demo.services;

import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom playlists generated from moods.
 * Stored in-memory for now.
 */
@Service
public class PlaylistService {

    private final PlaylistManager playlistManager;
    private final Map<String, Playlist> playlists = new LinkedHashMap<>();

    public PlaylistService(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

        public Playlist createPlaylist(String name, String description, Set<String> moods) {
        final Set<String> effectiveMoods = (moods == null) ? Set.of() : moods;

        String id = UUID.randomUUID().toString();
        // Create empty playlist; songs are added later by user choice.
        Playlist pl = new Playlist(id, name, description, new ArrayList<>());
        pl.setMoods(effectiveMoods);
        playlists.put(id, pl);
        return pl;
    }

    
    /**
     * Songs that match the playlist moods and are not already in the playlist.
     */
    public List<Song> getCandidateSongs(String playlistId) {
        Playlist pl = playlists.get(playlistId);
        if (pl == null) return List.of();

        Set<String> moods = pl.getMoods() == null ? Set.of() : pl.getMoods();
        Set<String> existingIds = pl.getSongs().stream()
                .filter(Objects::nonNull)
                .map(Song::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return playlistManager.getAllSongs().stream()
                .filter(s -> s != null && (moods.isEmpty() || moods.stream().anyMatch(s::hasMood)))
                .filter(s -> s.getId() == null || !existingIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    public Playlist addSongToPlaylist(String playlistId, String songId) {
        Playlist pl = playlists.get(playlistId);
        if (pl == null) return null;

        Song song = playlistManager.getAllSongs().stream()
                .filter(s -> s != null && Objects.equals(s.getId(), songId))
                .findFirst()
                .orElse(null);
        if (song == null) return pl;

        Set<String> moods = pl.getMoods() == null ? Set.of() : pl.getMoods();
        if (!moods.isEmpty() && moods.stream().noneMatch(song::hasMood)) {
            // Not compatible with playlist moods, ignore.
            return pl;
        }

        pl.addSong(song);
        return pl;
    }

    public Playlist removeSongFromPlaylist(String playlistId, String songId) {
        Playlist pl = playlists.get(playlistId);
        if (pl == null) return null;
        pl.removeSongById(songId);
        return pl;
    }
public List<Playlist> listPlaylists() {
        return new ArrayList<>(playlists.values());
    }

    public Playlist getPlaylist(String id) {
        return playlists.get(id);
    }

    public boolean deletePlaylist(String id) {
        return playlists.remove(id) != null;
    }
}
