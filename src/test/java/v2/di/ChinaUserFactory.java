package v2.di;

public class ChinaUserFactory {
    public static ChinaUser getChinaUser(String name, Cat cat) {
        return new ChinaUser(name, cat);
    }
}
