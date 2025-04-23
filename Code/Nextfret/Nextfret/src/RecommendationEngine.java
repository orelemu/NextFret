// RecommendationEngine.java
import java.sql.SQLException;
import java.util.List;

public class RecommendationEngine {
    private Db db;

    public RecommendationEngine(Db db) {
        this.db = db;
    }

    public List<Song> getRecommendedSongsForUser(User user) throws SQLException {
        return db.getSongsFromChords(user.getKnownChords());
    }
}
