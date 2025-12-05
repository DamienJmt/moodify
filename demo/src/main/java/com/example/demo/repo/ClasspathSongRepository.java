package com.example.demo.repo;

import com.example.demo.models.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * Loads songs from src/main/resources/data/songs.json.
 * Ensures deterministic IDs.
 */
@Repository
public class ClasspathSongRepository implements SongRepository {

    private final List<Song> songs;

    public ClasspathSongRepository() {
        this.songs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Song>> typeReference = new TypeReference<>() {};

        try {
            ClassPathResource resource = new ClassPathResource("data/songs.json");
            List<Song> loaded = mapper.readValue(resource.getInputStream(), typeReference);
            if (loaded != null) {
                for (Song s : loaded) {
                    if (s == null) continue;
                    s.normalizeSingleMood();
                    if (s.getId() == null || s.getId().isBlank()) {
                        s.setId(deterministicId(s));
                    }
                    songs.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Song> findAll() {
        return songs;
    }

    private String deterministicId(Song s) {
        String key = (s.getName() == null ? "" : s.getName().trim().toLowerCase())
                + "|" + (s.getArtists() == null ? "" : s.getArtists().trim().toLowerCase());
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(hash);
            return hex.substring(0, 12);
        } catch (Exception ex) {
            return key.replaceAll("\\s+", "_");
        }
    }
}
