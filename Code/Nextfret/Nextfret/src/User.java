// User.java
import java.util.List;
public class User {
    private int id;
    private String firstName, lastName, email;
    private List<Chord> knownChords;

    public User(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    public List<Chord> getKnownChords() { return knownChords; }
    public void setKnownChords(List<Chord> knownChords) {
        this.knownChords = knownChords;
    }
}
