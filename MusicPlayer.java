import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {

    private Clip clip;

    public void play(song song) {
        try {
            stop(); // sécurité : si un son jouait déjà
            File file = new File(song.getPath());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            System.out.println("Lecture de : " + song.getTitle());
        } catch (Exception e) {
            System.out.println("Erreur de lecture audio : " + e.getMessage());
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            System.out.println("=== Musique en pause ===");
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            System.out.println("=== Reprise de la lecture ===");
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            System.out.println("=== Lecture stoppée ===");
        }
    }
}
