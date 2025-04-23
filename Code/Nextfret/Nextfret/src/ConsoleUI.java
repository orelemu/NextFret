// ConsoleUI.java

import java.sql.SQLException;
import java.util.*;

public class ConsoleUI implements UserInterface {
    private Scanner scanner = new Scanner(System.in);
    private UserManager userManager;
    private RecommendationEngine recEngine;

    public ConsoleUI() {
        Db db = new Db();
        userManager = new UserManager(db);
        recEngine = new RecommendationEngine(db);
    }

    @Override
    public void showSign() {
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.print("> ");
        int c = scanner.nextInt();
        scanner.nextLine();
        try {
            if (c == 1) signIn();
            else signUp();
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
            showSign();
        }
    }

    @Override
    public void signUp() throws SQLException {
        System.out.print("First name: ");
        String fn = scanner.nextLine();
        System.out.print("Last  name: ");
        String ln = scanner.nextLine();
        System.out.print("Email     : ");
        String mail = scanner.nextLine();
        System.out.print("Password  : ");
        String pwd = scanner.nextLine();
        userManager.signUp(fn, ln, mail, pwd);
        System.out.println("Signed up as " + fn);
    }

    @Override
    public void signIn() throws SQLException {
        System.out.print("Email    : ");
        String mail = scanner.nextLine();
        System.out.print("Password : ");
        String pwd = scanner.nextLine();
        User u = userManager.signIn(mail, pwd);
        if (u == null) {
            System.out.println("Invalid credentials. Try again.");
            showSign();
        } else {
            System.out.println("Welcome back, " + u.getFirstName());
        }
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("""
                    === Menu ===
                    1. Add known chords
                    2. Recommend songs
                    3. Show chords & lyrics of a song
                    4. Show my chords
                    5. Exit
                    """);
            System.out.print("> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            try {
                switch (choice) {
                    case 1 -> addKnownChords();
                    case 2 -> recommendSongs();
                    case 3 -> showChordsAndTabs();
                    case 4 -> showFavorites();  // מכאן נוסיף תצוגת אקורדים
                    case 5 -> {
                        System.out.println("bye");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println(" " + e.getMessage());
            }
        }
    }

    @Override
    public void addKnownChords() throws SQLException {
        System.out.print("Enter chords you know (comma-separated): ");
        String line = scanner.nextLine();
        List<String> names = Arrays.asList(line.split(","));
        userManager.addKnownChords(names);
        System.out.println(" Updated known chords: " + userManager.getKnownChords());
    }

    @Override
    public void recommendSongs() throws SQLException {
        List<Song> recs = recEngine.getRecommendedSongsForUser(userManager.getCurrentUser());
        if (recs.isEmpty()) {
            System.out.println("No recommendations (maybe add more chords?).");
        } else {
            System.out.println("Recommended:");
            for (Song s : recs) {
                System.out.println("- " + s);
            }
        }
    }

    @Override
    public void showChordsAndTabs() throws SQLException {
        System.out.print("Song title: ");
        String title = scanner.nextLine();
        Song s = userManager.getSongByTitle(title);
        if (s == null) {
            System.out.println("No such song.");
        } else {
            System.out.println("=== " + s.getTitle() + " ===");
            System.out.println("Chords: " + s.getChordList());
            System.out.println("--- Lyrics ---\n" + s.getLyrics());
        }
    }

    @Override
    public void showFavorites() {
        // בינתיים מציג את אקורדי המשתמש
        try {
            System.out.println("Your chords: " + userManager.getKnownChords());
        } catch (SQLException e) {
            System.out.println(" " + e.getMessage());
        }
    }
}
