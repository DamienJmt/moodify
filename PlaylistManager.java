import java.io.BufferedReader;
import java.io.FileReader;

public class PlaylistManager {

    public playlist loadFromFile(String filePath) {
        playlist playlist = new playlist();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length == 3) {
                    String title = parts[0];
                    String path = parts[1];
                    String humeur = parts[2];

                    song song = new song(title, path, humeur);
                    playlist.addSong(song);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playlist;
    }
}
