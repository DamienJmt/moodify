package com.example.demo.repo;

import com.example.demo.models.Song;
import java.util.List;

public interface SongRepository {
    List<Song> findAll();
}
