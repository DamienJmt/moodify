package com.example.demo.services;

import com.example.demo.models.Playlist;
import com.example.demo.models.Song;
import com.example.demo.repo.SongRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    private final SongRepository repo;

    public PlaylistService(SongRepository repo) {
        this.repo = repo;
    }

    public Playlist createByMoods(String name, String description, Collection<String> moods) {
        Playlist p = new Playlist(UUID.randomUUID().toString(), name, description);
        List<Song> all = repo.findAll();
        Set<String> wanted = moods == null
                ? Set.of()
                : moods.stream().map(String::toLowerCase).collect(Collectors.toSet());

        for (Song s : all) {
            boolean match = wanted.isEmpty() || s.getMoods().stream().map(String::toLowerCase).anyMatch(wanted::contains);
            if (match) p.addSong(s);
        }
        return p;
    }

    public Playlist createManual(String name, String description, Collection<String> songIds) {
        Playlist p = new Playlist(UUID.randomUUID().toString(), name, description);
        if (songIds != null) {
            for (String id : songIds) repo.findById(id).ifPresent(p::addSong);
        }
        return p;
    }
}
