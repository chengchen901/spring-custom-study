package v2.di;

public class ChinaUserFactoryBean {
    public ChinaUser getChinaUser(String name, Cat cat) {
        return new ChinaUser(name, cat);
    }
}
