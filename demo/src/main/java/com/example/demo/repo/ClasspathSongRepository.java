package com.example.demo.repo;

import com.example.demo.models.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

/**
 * Repo basé sur le fichier songs.json du classpath.
 * Lecture au démarrage, stockage en mémoire.
 * (Pas de persistance disque, car resources en lecture seule.)
 */
@Repository
public class ClasspathSongRepository implements SongRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Song> cache = new LinkedHashMap<>();

    public ClasspathSongRepository() {
        load();
    }

    @Override
    public List<Song> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Song> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void saveAll(Collection<Song> songs) {
        if (songs == null) return;
        for (Song s : songs) {
            if (s.getId() == null || s.getId().isBlank()) {
                s.setId(UUID.randomUUID().toString());
            }
            cache.put(s.getId(), s);
        }
    }

    private void load() {
        try {
            ClassPathResource resource = new ClassPathResource("data/songs.json");
            List<Song> songs = mapper.readValue(resource.getInputStream(), new TypeReference<List<Song>>() {});
            saveAll(songs);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
