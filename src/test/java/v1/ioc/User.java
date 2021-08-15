package v1.ioc;

public class User {
    private String name;

    public User() {
        this.name = "构造方法方式";
    }

    public User(String name) {
        this.name = name;
    }

    public void doSomething() {
        System.out.println(name + ", hashCode:" + hashCode());
    }

    public void init() {
        System.out.println(name + "执行初始化方法...");
    }

    public void destroy() {
        System.out.println(name + "执行销毁方法...");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ioc.User{" +
                "name='" + name + '\'' +
                '}';
    }
}
