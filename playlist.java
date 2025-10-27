import java.util.ArrayList;

public class playlist {

    private ArrayList<song> songs;
    private int indexCourant = 0;

    public playlist() {
        this.songs = new ArrayList<>();
    }

    public void addSong(song song) {
        songs.add(song);
    }

    public void removeSong(song song) {
        songs.remove(song);
    }

    public song next() {
        if (songs.isEmpty()) return null;
        indexCourant = (indexCourant + 1) % songs.size();
        return songs.get(indexCourant);
    }

    public song previous() {
        if (songs.isEmpty()) return null;
        indexCourant = (indexCourant - 1 + songs.size()) % songs.size();
        return songs.get(indexCourant);
    }

    public song getCurrentSong() {
        if (songs.isEmpty()) return null;
        return songs.get(indexCourant);
    }
}
