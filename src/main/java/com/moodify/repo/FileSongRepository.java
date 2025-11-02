package com.moodify.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodify.model.Song;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileSongRepository implements SongRepository {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Song> cache = new HashMap<>();

    public FileSongRepository(Path file) { this.file = file; load(); }

    @Override public List<Song> findAll() { return new ArrayList<>(cache.values()); }
    @Override public Optional<Song> findById(String id) { return Optional.ofNullable(cache.get(id)); }
    @Override public void saveAll(Collection<Song> songs) {
        for (Song s : songs) {
            if (s.getId() == null || s.getId().isBlank()) s.setId(UUID.randomUUID().toString());
            cache.put(s.getId(), s);
        }
        persist();
    }

    private void load() {
        if (!Files.exists(file)) return;
        try (InputStream in = Files.newInputStream(file)) {
            List<Song> list = mapper.readValue(in, new TypeReference<List<Song>>(){});
            for (Song s : list) cache.put(s.getId(), s);
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    private void persist() {
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(out, cache.values());
            }
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }
}
