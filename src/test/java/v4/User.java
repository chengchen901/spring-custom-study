package v4;

import com.study.context.config.annotation.Component;

@Component(initMethodName = "init", destroyMethodName = "destroy")
public class User {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public void init() {
        this.name = "Hash";
        this.age = 18;
        System.out.println(name + "执行初始化方法...");
    }

    public void destroy() {
        System.out.println(name + "执行销毁方法...");
    }
}
