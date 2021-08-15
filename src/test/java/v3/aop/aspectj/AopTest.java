package v3.aop.aspectj;

import com.study.aop.AdvisorAutoProxyCreator;
import com.study.aop.advisor.AspectJPointcutAdvisor;
import com.study.beans.*;
import org.junit.Test;
import v1.ioc.User;
import v1.ioc.UserFactory;
import v2.di.ChinaUser;
import v2.di.GarfieldCat;
import v3.aop.aspectj.advice.MyAfterReturningAdvice;
import v3.aop.aspectj.advice.MyBeforeAdvice;
import v3.aop.aspectj.advice.MyMethodInterceptor;

import java.util.Arrays;
import java.util.List;

public class AopTest {

    static PreBuildBeanFactory bf = new PreBuildBeanFactory();

    @Test
    public void aopTest() throws Throwable {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(ChinaUser.class);
        List<Object> args = Arrays.asList("构造方式DI");
        bd.setConstructorArgumentValues(args);
        List<PropertyValue> pvs = Arrays.asList(new PropertyValue("cat", new BeanReference("garfieldCat")));
        bd.setPropertyValues(pvs);
        bf.registryBeanDefinition("chinaUser", bd);

        bd = new GenericBeanDefinition();
        bd.setBeanClass(GarfieldCat.class);
        args = Arrays.asList("罐头");
        bd.setConstructorArgumentValues(args);
        pvs = Arrays.asList(new PropertyValue("master", new BeanReference("chinaUser")));
        bd.setPropertyValues(pvs);
        bf.registryBeanDefinition("garfieldCat", bd);

        // 前置增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyBeforeAdvice.class);
        bf.registryBeanDefinition("myBeforeAdvice", bd);

        // 环绕增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyMethodInterceptor.class);
        bf.registryBeanDefinition("myMethodInterceptor", bd);

        // 后置增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyAfterReturningAdvice.class);
        bf.registryBeanDefinition("myAfterReturningAdvice", bd);

        final AdvisorAutoProxyCreator aapc = new AdvisorAutoProxyCreator();
        bf.registerBeanPostProcessor(aapc);
        // 向AdvisorAutoProxyCreator注册Advisor
        aapc.registAdvisor(new AspectJPointcutAdvisor("myBeforeAdvice", "execution(* v2.di.ChinaUser.toString(..))"));
        aapc.registAdvisor(new AspectJPointcutAdvisor("myMethodInterceptor", "execution(* v2.di.ChinaUser.getCat(..))"));
        aapc.registAdvisor(new AspectJPointcutAdvisor("myAfterReturningAdvice", "execution(* v2.di.GarfieldCat.toString(..))"));

        bf.preInstantiateSingletons();

        System.out.println("-----------------myBeforeAdvice---------------");
        ChinaUser chinaUser = (ChinaUser) bf.getBean("chinaUser");
        System.out.println(chinaUser.toString());

        System.out.println("----------------myMethodInterceptor----------------");
        GarfieldCat cat = (GarfieldCat) chinaUser.getCat();
        System.out.println(cat.getName());

        System.out.println("-----------------myAfterReturningAdvice---------------");
        GarfieldCat garfieldCat = (GarfieldCat) bf.getBean("garfieldCat");
        System.out.println(garfieldCat.toString());
    }

    @Test
    public void createBeanByStaticFactoryTest() throws Exception {
        BeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(UserFactory.class);
        bd.setFactoryMethodName("createUser");
        List<Object> args = Arrays.asList("静态工厂方法方式传入构造参数");
        bd.setConstructorArgumentValues(args);
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bd.setInitMethod("init");
        bd.setDestroyMethod("destroy");
        bf.registryBeanDefinition("staticFactoryUser", bd);

        // 前置增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyBeforeAdvice.class);
        bf.registryBeanDefinition("myBeforeAdvice", bd);

        // 环绕增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyMethodInterceptor.class);
        bf.registryBeanDefinition("myMethodInterceptor", bd);

        // 后置增强advice bean注册
        bd = new GenericBeanDefinition();
        bd.setBeanClass(MyAfterReturningAdvice.class);
        bf.registryBeanDefinition("myAfterReturningAdvice", bd);

        final AdvisorAutoProxyCreator aapc = new AdvisorAutoProxyCreator();
        bf.registerBeanPostProcessor(aapc);
        // 向AdvisorAutoProxyCreator注册Advisor
        aapc.registAdvisor(new AspectJPointcutAdvisor("myBeforeAdvice", "execution(* v1.ioc.User.doSomething(..))"));
        aapc.registAdvisor(new AspectJPointcutAdvisor("myAfterReturningAdvice", "execution(* v1.ioc.User.doSomething(..))"));
        aapc.registAdvisor(new AspectJPointcutAdvisor("myMethodInterceptor", "execution(* v1.ioc.User.doSomething(..))"));

        User user = (User) bf.getBean("staticFactoryUser");
        user.doSomething();
    }
}
