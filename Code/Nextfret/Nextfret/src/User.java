import java.util.List;

public class User {
    private List<Chord> knwonChords;
    private List<Song> knownSongs;
    private String name;

    public List<Chord> getKnwonChords() {
        return knwonChords;
    }

    public void setKnwonChords(List<Chord> knwonChords) {
        this.knwonChords = knwonChords;
    }

    public List<Song> getKnownSongs() {
        return knownSongs;
    }

    public void setKnownSongs(List<Song> knownSongs) {
        this.knownSongs = knownSongs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
