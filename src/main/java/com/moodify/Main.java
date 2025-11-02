package com.moodify;

import com.moodify.audio.NoopAudioPlayer;
import com.moodify.model.Playlist;
import com.moodify.model.Song;
import com.moodify.repo.FileSongRepository;
import com.moodify.repo.SongRepository;
import com.moodify.service.PlaybackService;
import com.moodify.service.PlaylistService;

import java.nio.file.Path;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SongRepository repo = new FileSongRepository(Path.of("data/songs.json"));
        PlaylistService pl = new PlaylistService(repo);
        Playlist mood = pl.createByMoods("Chill Vibes", "Relax & study", Arrays.asList("chill", "calm", "relax"));
        PlaybackService pb = new PlaybackService(new NoopAudioPlayer());
        pb.setPlaylist(mood);
        pb.playCurrent().ifPresent(s -> System.out.println("Playing: " + s));
    }
}
