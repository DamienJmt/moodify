package com.moodify.repo;

import com.moodify.model.Song;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySongRepository implements SongRepository {
    private final Map<String, Song> store = new ConcurrentHashMap<>();

    @Override public List<Song> findAll() { return new ArrayList<>(store.values()); }
    @Override public Optional<Song> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public void saveAll(Collection<Song> songs) {
        for (Song s : songs) {
            String id = s.getId();
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
                s.setId(id);
            }
            store.put(id, s);
        }
    }
}
