package v1.ioc;

public class UserFactoryBean {

    public User createUser() {
        return new User("工厂方法方式");
    }
}
