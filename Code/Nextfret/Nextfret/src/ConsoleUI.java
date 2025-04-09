import java.util.Scanner;

public class ConsoleUI implements UserInterface {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void showSign(){
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> signIn();
            case 2 -> signUp();
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    @Override
    public void showMenu() {
        int choice;
        do {
            System.out.println("=== Next Fret App Menu ===");
            System.out.println("1. Add chords you know");
            System.out.println("2. Recommend a song based on my chords");
            System.out.println("3. Show chords and tabs for a song");
            System.out.println("4. Show favorite songs");
            System.out.println("5. Log Out");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addKnownChords();
                case 2 -> recommendSongs();
                case 3 -> showChordsAndTabs();

                case 4 -> showFavorites();
                case 5 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }

            System.out.println(); // Line break
        } while (choice != 6);
    }

    @Override
    public void addKnownChords() {
        //printChordList();
        //selectKnownChords();
        //updateDb();
    }

    @Override
    public void recommendSongs() {
        // SongList = getRecomendedSongsForUser(userId);
        // ShowSongs(SongList);
    }

    @Override
    public void showChordsAndTabs() {
        //song = selectSong();
        //showSongMenu(song);
    }

    @Override
    public void showFavorites() {
        System.out.println("Favorite songs:");
    }

    @Override
    public void signUp(){
        String first_name, last_name, mail, password;
        System.out.print("Enter your first name: ");
        first_name = scanner.nextLine();
        System.out.print("Enter your last name: ");
        last_name = scanner.nextLine();
        System.out.print("Enter your mail: ");
        mail = scanner.nextLine();
        System.out.print("Enter your password: ");
        password = scanner.nextLine();
        //createUserInDb(first_name, last_name, mail, password);
    }
    @Override
    public void signIn(){
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        //getUserFromDb(email,password);
    }
}
