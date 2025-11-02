package com.moodify.service;

import com.moodify.model.Playlist;
import com.moodify.model.Song;
import com.moodify.repo.SongRepository;
import java.util.*;
import java.util.stream.Collectors;

public class PlaylistService {
    private final SongRepository repo;

    public PlaylistService(SongRepository repo) { this.repo = repo; }

    public Playlist createByMoods(String name, String description, Collection<String> moods) {
        Playlist p = new Playlist(UUID.randomUUID().toString(), name, description);
        List<Song> all = repo.findAll();
        Set<String> wanted = moods.stream().map(String::toLowerCase).collect(Collectors.toSet());
        for (Song s : all) {
            boolean match = s.getMoods().stream().map(String::toLowerCase).anyMatch(wanted::contains);
            if (match) p.addSong(s);
        }
        return p;
    }

    public Playlist createManual(String name, String description, Collection<String> songIds) {
        Playlist p = new Playlist(UUID.randomUUID().toString(), name, description);
        for (String id : songIds) {
            repo.findById(id).ifPresent(p::addSong);
        }
        return p;
    }
}
