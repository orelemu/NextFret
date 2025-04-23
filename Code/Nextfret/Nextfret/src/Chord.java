// Chord.java
public class Chord {
    private int id;
    private String name;

    public Chord(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Chord(String name) {
        this(0, name);
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chord)) return false;
        Chord chord = (Chord) o;
        return name.equals(chord.name);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
