import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new ConsoleUI();
        ui.showSign();
        while (true){
            ui.showMenu();
        }
    }

    private static void initDatabaseSongsFromDir(){
        File rootFolder = new File("/Users/yuvalgreenberg/Desktop/chrods/OLGA 2");
        Db db = new Db();
        ChordProParser parser = new ChordProParser();
        RecommendationEngine recommendationEngine = new RecommendationEngine(db);
        processFolderRecursively(rootFolder, db, parser);
    }

    private static void processFolderRecursively(File folder, Db db, ChordProParser parser) {
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("❌ התיקייה לא קיימת או לא תקינה: " + folder.getAbsolutePath());
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                processFolderRecursively(file, db, parser);
            } else if (file.getName().toLowerCase().endsWith(".chopro")) {
                try {
                    ChordProParser.SongData song = parser.parseFile(file);
                    db.insertSongFromParsedData(song);
                    System.out.println("✅ הוזן שיר: " + song.title + " (" + song.artist + ")");
                } catch (IOException e) {
                    System.out.println("⚠️ שגיאת קריאה בקובץ: " + file.getAbsolutePath());
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.out.println("⚠️ שגיאת SQL עבור הקובץ: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }
}
