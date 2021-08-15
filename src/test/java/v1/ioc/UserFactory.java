package v1.ioc;

public class UserFactory {

    public static User createUser() {
        return new User("静态工厂方法方式");
    }

    public static User createUser(String name) {
        return new User(name);
    }
}
