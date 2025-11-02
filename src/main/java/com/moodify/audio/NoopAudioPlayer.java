package com.moodify.audio;

import com.moodify.model.Song;
import java.util.Optional;

/**
 * Classe simulant le fonctionnement de AudioPlayer sans opération (No Operation > Noop).
 * Utile pour les test unitaires.
 */
public class NoopAudioPlayer implements AudioPlayer {
    private Song current;
    private boolean playing;

    @Override public void play(Song song) { this.current = song; this.playing = true; }
    @Override public void pause() { this.playing = false; }
    @Override public void resume() { if (current != null) this.playing = true; }
    @Override public void stop() { this.playing = false; this.current = null; }
    @Override public void seekSeconds(int seconds) {}
    @Override public void setVolume(double volume) {}
    @Override public boolean isPlaying() { return playing; }
    @Override public Optional<Song> getCurrent() { return Optional.ofNullable(current); }
}
