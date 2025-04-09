import java.sql.*;

public class Db {
    private static final String URL = "jdbc:postgresql://localhost:5432/Nextfret";
    private static final String USER = "postgres";
    private static final String PASSWORD = "yuval2119";
    private Connection connection;

    private void establishConnection() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Error connecting to the database: " + e.getMessage());
            this.connection = null;
        }
    }

    public Db() {
        this.establishConnection();
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

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setInt(2, artistId);
            ps.setString(3, lyrics);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            // אם השיר כבר קיים — שלוף את ה-ID
            try (PreparedStatement ps2 = connection.prepareStatement(
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
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            try (PreparedStatement ps2 = connection.prepareStatement("SELECT id FROM artists WHERE name = ?")) {
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
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            try (PreparedStatement ps2 = connection.prepareStatement("SELECT id FROM chords WHERE name = ?")) {
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ps.setInt(2, songId);
            ps.executeUpdate();
        }
    }

    private void linkSongChord(int songId, int chordId) throws SQLException {
        String sql = "INSERT INTO song_chords (song_id, chord_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, songId);
            ps.setInt(2, chordId);
            ps.executeUpdate();
        }
    }
}
