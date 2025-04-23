import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Db {
    private static final String URL = "jdbc:postgresql://localhost:5432/Nextfret";
    private static final String USER = "postgres";
    private static final String PASSWORD = "yuval2119";
    private Connection conn;

    private void establishConnection() {
        try {
            this.conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Error connecting to the database: " + e.getMessage());
            this.conn = null;
        }
    }


    // פונקציה עיקרית
    public void insertSongFromParsedData(ChordProParser.SongData songData) throws SQLException {
        int artistId = getOrInsertArtist(songData.artist);          // קודם כל אמן
        int songId = insertSong(songData.title, artistId, songData.lyrics);
        // ואז מכניסים שיר עם artist_id
        linkArtistSong(artistId, songId);                           // ממשיכים כרגיל

        for (String chord : songData.chords) {
            int chordId = getOrInsertChord(chord);
            linkSongChord(songId, chordId);
        }
    }


    private int insertSong(String title, int artistId, String lyrics) throws SQLException {
        String sql = """
                    INSERT INTO songs (title, artist_id, lyrics)
                    VALUES (?, ?, ?)
                    ON CONFLICT (title, artist_id)
                    DO UPDATE SET lyrics = EXCLUDED.lyrics
                    RETURNING id
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setInt(2, artistId);
            ps.setString(3, lyrics);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            // אם השיר כבר קיים — שלוף את ה-ID
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT id FROM songs WHERE title = ? AND artist_id = ?")) {
                ps2.setString(1, title);
                ps2.setInt(2, artistId);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }

        throw new SQLException("⚠️ Failed to get song ID for: " + title + " (artist_id=" + artistId + ")");
    }


    private int getOrInsertArtist(String name) throws SQLException {
        String sql = "INSERT INTO artists (name) VALUES (?) ON CONFLICT (name) DO NOTHING RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            try (PreparedStatement ps2 = conn.prepareStatement("SELECT id FROM artists WHERE name = ?")) {
                ps2.setString(1, name);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }
        throw new SQLException("⚠️ Failed to get artist ID for name: " + name);
    }

    private int getOrInsertChord(String name) throws SQLException {
        String sql = "INSERT INTO chords (name) VALUES (?) ON CONFLICT (name) DO NOTHING RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            try (PreparedStatement ps2 = conn.prepareStatement("SELECT id FROM chords WHERE name = ?")) {
                ps2.setString(1, name);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }
        throw new SQLException("⚠️ Failed to get chord ID for name: " + name);
    }

    private void linkArtistSong(int artistId, int songId) throws SQLException {
        String sql = "INSERT INTO artist_songs (artist_id, song_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ps.setInt(2, songId);
            ps.executeUpdate();
        }
    }

    private void linkSongChord(int songId, int chordId) throws SQLException {
        String sql = "INSERT INTO song_chords (song_id, chord_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, songId);
            ps.setInt(2, chordId);
            ps.executeUpdate();
        }
    }


    public List<Chord> getChordsForUser() {
        List<Chord> chords = null;
        return chords;
    }

    public Db() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Database connected successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- users ---
    public User insertUser(String firstName, String lastName, String email, String password) throws SQLException {
        String sql = """
                    INSERT INTO users (first_name, last_name, email, password)
                    VALUES (?, ?, ?, ?)
                    RETURNING id
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                rs.next();
                return new User(rs.getInt(1), firstName, lastName, email);
            }
        }
    }

    public User getUser(String email, String password) throws SQLException {
        String sql = "SELECT id, first_name, last_name FROM users WHERE email=? AND password=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), email);
                }
            }
        }
        return null;
    }

    // --- user chords ---
    public void addKnownChord(int userId, String chordName) throws SQLException {
        int chordId = getOrInsertChord(chordName);
        String sql = """
                    INSERT INTO user_chords (user_id, chord_id)
                    VALUES (?, ?)
                    ON CONFLICT DO NOTHING
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, chordId);
            ps.executeUpdate();
        }
    }

    public List<Chord> getChordsForUser(int userId) throws SQLException {
        String sql = """
                    SELECT c.id, c.name
                    FROM chords c
                    JOIN user_chords uc ON c.id = uc.chord_id
                    WHERE uc.user_id = ?
                """;
        List<Chord> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Chord(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return list;
    }

    // --- recommendation: songs matching known chords, sorted by # of matches desc ---
    public List<Song> getSongsFromChords(List<Chord> knownChords) throws SQLException {
        if (knownChords.isEmpty()) return Collections.emptyList();

        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < knownChords.size(); i++) {
            inClause.append("?");
            if (i < knownChords.size() - 1) inClause.append(",");
        }

        String sql = " SELECT s.id, s.title, s.lyrics, COUNT(*) AS matches FROM songs s JOIN song_chords sc ON s.id = sc.song_id JOIN chords c ON sc.chord_id = c.id WHERE c.name IN (" + inClause + ") GROUP BY s.id ORDER BY matches DESC LIMIT 10 ";

        List<Song> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < knownChords.size(); i++) {
                ps.setString(i + 1, knownChords.get(i).getName());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int songId = rs.getInt("id");
                    String title = rs.getString("title");
                    String lyrics = rs.getString("lyrics");
                    // קחו גם אקורדים מלאים לשיר
                    List<Chord> chords = getChordsForSong(songId);
                    list.add(new Song(songId, title, lyrics, chords));
                }
            }
        }
        return list;
    }

    private List<Chord> getChordsForSong(int songId) throws SQLException {
        String sql = """
                    SELECT c.id, c.name
                    FROM chords c
                    JOIN song_chords sc ON c.id = sc.chord_id
                    WHERE sc.song_id = ?
                """;
        List<Chord> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, songId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Chord(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return list;
    }

    // --- song lookup by title ---
    public Song getSongByTitle(String title) throws SQLException {
        String sql = "SELECT id, lyrics FROM songs WHERE title = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String lyrics = rs.getString("lyrics");
                    List<Chord> chords = getChordsForSong(id);
                    return new Song(id, title, lyrics, chords);
                }
            }
        }
        return null;
    }
}


