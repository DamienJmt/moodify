package com.moodify.service;

import com.moodify.audio.AudioPlayer;
import com.moodify.model.Playlist;
import com.moodify.model.Song;

import java.util.Optional;

public class PlaybackService {
    private final AudioPlayer player;
    private Playlist playlist;

    public PlaybackService(AudioPlayer player) { this.player = player; }
    public void setPlaylist(Playlist playlist) { this.playlist = playlist; }

    public Optional<Song> playCurrent() {
        if (playlist == null) return Optional.empty();
        return playlist.getCurrentSong().map(s -> { player.play(s); return s; });
    }
    public Optional<Song> next() {
        if (playlist == null) return Optional.empty();
        Optional<Song> s = playlist.next();
        s.ifPresent(player::play);
        return s;
    }
    public Optional<Song> previous() {
        if (playlist == null) return Optional.empty();
        Optional<Song> s = playlist.previous();
        s.ifPresent(player::play);
        return s;
    }
    public void pause() { player.pause(); }
    public void resume() { player.resume(); }
    public void stop() { player.stop(); }
}
