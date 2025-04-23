import java.sql.SQLException;

public interface UserInterface {

    void showSign();

    void showMenu();

    void addKnownChords() throws SQLException;

    void recommendSongs() throws SQLException;

    void showChordsAndTabs() throws SQLException;

    void signUp() throws SQLException;

    void signIn() throws SQLException;

    void showFavorites();
}
