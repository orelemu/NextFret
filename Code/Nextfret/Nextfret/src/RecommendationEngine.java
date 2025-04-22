import java.util.List;


public class RecommendationEngine {
    Db database;

    public RecommendationEngine(Db database) {
        this.database = database;
    }

    public List<Song> getReccomendedSongsForUser(User user) {
        List<Song> songs = database.getSongsFromChords(user.getKnwonChords());
        return songs;
    }
}


