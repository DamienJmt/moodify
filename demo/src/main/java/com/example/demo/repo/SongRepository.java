package com.example.demo.repo;

import com.example.demo.models.Song;

import java.util.*;

public interface SongRepository {
    List<Song> findAll();
    Optional<Song> findById(String id);
    void saveAll(Collection<Song> songs);
}
