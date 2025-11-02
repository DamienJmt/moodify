package com.moodify.audio;

import com.moodify.model.Song;
import java.util.Optional;

public interface AudioPlayer {
    void play(Song song);
    void pause();
    void resume();
    void stop();
    void seekSeconds(int seconds);
    void setVolume(double volume); // De 1 à 0
    boolean isPlaying();
    Optional<Song> getCurrent();
}
