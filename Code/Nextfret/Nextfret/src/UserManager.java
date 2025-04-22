import java.util.ArrayList;
import java.util.List;

public class UserManager {
    Db database;

    public UserManager(Db database) {
        this.database = database;
    }

    public List<Chord> getChordsForUser() {
        List<Chord> songs = database.getChordsForUser();
        return songs;
    }
}
