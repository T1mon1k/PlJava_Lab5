import java.io.Serializable;

public class DataItem implements Serializable {
    private final int id;
    private final String name;

    public DataItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}
