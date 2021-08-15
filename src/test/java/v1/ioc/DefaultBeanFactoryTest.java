package v1.ioc;

import com.study.beans.BeanDefinition;
import com.study.beans.GenericBeanDefinition;
import com.study.beans.PreBuildBeanFactory;
import org.junit.AfterClass;
import org.junit.Test;

public class DefaultBeanFactoryTest {

    static PreBuildBeanFactory bf = new PreBuildBeanFactory();

    @Test
    public void createBeanByConstructorTest() throws Exception {
        BeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(User.class);
        bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bd.setInitMethod("init");
        bd.setDestroyMethod("destroy");
        bf.registryBeanDefinition("user", bd);
    }

    @Test
    public void createBeanByStaticFactoryTest() throws Exception {
        BeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(UserFactory.class);
        bd.setFactoryMethodName("createUser");
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bd.setInitMethod("init");
        bd.setDestroyMethod("destroy");
        bf.registryBeanDefinition("staticFactoryUser", bd);
    }

    @Test
    public void createBeanByBeanFactoryTest() throws Exception {
        BeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(UserFactoryBean.class);
        String factoryBeanName = "factoryBean";
        bf.registryBeanDefinition(factoryBeanName, bd);

        bd = new GenericBeanDefinition();
        bd.setFactoryBeanName(factoryBeanName);
        bd.setFactoryMethodName("createUser");
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bf.registryBeanDefinition("factoryBeanUser", bd);
    }

    @AfterClass
    public static void getBeanTest() throws Exception {
        bf.preInstantiateSingletons();

        System.out.println("=========================================构造方法方式=========================================");
        for (int i = 0; i < 3; i++) {
            User user = (User) bf.getBean("user");
            user.doSomething();
        }

        System.out.println("=========================================静态工厂方法方式=========================================");
        for (int i = 0; i < 3; i++) {
            User user = (User) bf.getBean("staticFactoryUser");
            user.doSomething();
        }

        System.out.println("=========================================工厂方法方式=========================================");
        for (int i = 0; i < 3; i++) {
            User user = (User) bf.getBean("factoryBeanUser");
            user.doSomething();
        }

        System.out.println("=========================================关闭bean容器=========================================");
        bf.close();
    }
}
