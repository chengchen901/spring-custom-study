package v2.di;

public class GarfieldCat implements Cat {

    private String name;

    private User master;

    public GarfieldCat(String name) {
        this.name = name;
    }

    public GarfieldCat(String name, User master) {
        this.name = name;
        this.master = master;
    }

    public String getName() {
        return name;
    }

    public User getMaster() {
        return master;
    }

    @Override
    public String toString() {
        return "GarfieldCat{" +
                "name='" + name + '\'' +
                ", master=" + master +
                '}';
    }
}
