package com.example.demo.Controller;

import com.example.demo.models.Playlist;
import com.example.demo.services.PlaylistService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public static class CreatePlaylistRequest {
        @NotBlank
        public String name;

        public String description;

        @Size(min = 0)
        public Set<String> moods;
    }

    @PostMapping
    public Playlist create(@RequestBody CreatePlaylistRequest req) {
        return playlistService.createPlaylist(req.name, req.description, req.moods);
    }

    @GetMapping
    public List<Playlist> list() {
        return playlistService.listPlaylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> get(@PathVariable String id) {
        Playlist pl = playlistService.getPlaylist(id);
        return pl == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(pl);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return playlistService.deletePlaylist(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
