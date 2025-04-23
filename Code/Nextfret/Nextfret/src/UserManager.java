// UserManager.java
import java.sql.SQLException;
import java.util.List;

public class UserManager {
    private Db db;
    private User currentUser;

    public UserManager(Db db) {
        this.db = db;
    }

    public User signUp(String fn, String ln, String email, String pwd) throws SQLException {
        currentUser = db.insertUser(fn, ln, email, pwd);
        return currentUser;
    }
    public User signIn(String email, String pwd) throws SQLException {
        currentUser = db.getUser(email, pwd);
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addKnownChords(List<String> chordNames) throws SQLException {
        for (String name : chordNames) {
            db.addKnownChord(currentUser.getId(), name.trim());
        }
        // רענון מבטא
        List<Chord> updated = db.getChordsForUser(currentUser.getId());
        currentUser.setKnownChords(updated);
    }

    public List<Chord> getKnownChords() throws SQLException {
        List<Chord> list = db.getChordsForUser(currentUser.getId());
        currentUser.setKnownChords(list);
        return list;
    }

    public Song getSongByTitle(String title) throws SQLException {
        return db.getSongByTitle(title);
    }

}
