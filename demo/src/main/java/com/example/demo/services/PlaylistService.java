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
        if (moods == null) moods = Set.of();
        List<Song> selected = playlistManager.getAllSongs().stream()
                .filter(s -> moods.isEmpty() || moods.stream().anyMatch(s::hasMood))
                .collect(Collectors.toList());

        String id = UUID.randomUUID().toString();
        Playlist pl = new Playlist(id, name, description, selected);
        playlists.put(id, pl);
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
