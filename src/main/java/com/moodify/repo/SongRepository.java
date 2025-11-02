package com.moodify.repo;

import com.moodify.model.Song;
import java.util.*;

public interface SongRepository {
    List<Song> findAll();
    Optional<Song> findById(String id);
    void saveAll(Collection<Song> songs);
}
