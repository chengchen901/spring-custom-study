package v2.di;

public class ChinaUser implements User {

    private String name;

    private Cat cat;

    public ChinaUser() {
    }

    public ChinaUser(String name) {
        this.name = name;
    }

    public ChinaUser(String name, Cat cat) {
        this.name = name;
        this.cat = cat;
    }

    public ChinaUser(String name, GarfieldCat cat) {
        this.name = name;
        this.cat = cat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public void init() {
        System.out.println(name + "执行初始化方法...");
    }

    public void destroy() {
        System.out.println(name + "执行销毁方法...");
    }

    @Override
    public String toString() {
        return "ChinaUser{" +
                "name='" + name + '\'' +
                ", cat=" + cat +
                '}';
    }
}
