// Song.java
import java.util.List;
public class Song {
    private int id;
    private String title;
    private String lyrics;
    private List<Chord> chordList;

    public Song(int id, String title, String lyrics, List<Chord> chordList) {
        this.id = id;
        this.title = title;
        this.lyrics = lyrics;
        this.chordList = chordList;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLyrics() { return lyrics; }
    public List<Chord> getChordList() { return chordList; }

    @Override
    public String toString() {
        return title + " – אקורדים: " + chordList;
    }
}
