package v2.di;

import com.study.beans.BeanReference;
import com.study.beans.GenericBeanDefinition;
import com.study.beans.PreBuildBeanFactory;
import com.study.beans.PropertyValue;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DITest {

    static PreBuildBeanFactory bf = new PreBuildBeanFactory();

    @Test
    public void createBeanByConstructorDITest() throws Exception {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(ChinaUser.class);
        List<Object> args = Arrays.asList("构造方式DI", new BeanReference("garfieldCat"));
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("chinaUser", bd);

        bd = new GenericBeanDefinition();
        bd.setBeanClass(GarfieldCat.class);
        args = Arrays.asList("罐头");
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("garfieldCat", bd);

        bf.preInstantiateSingletons();

        ChinaUser chinaUser = (ChinaUser) bf.getBean("chinaUser");
        System.out.println(chinaUser.toString());
    }

    @Test
    public void createBeanByStaticFactoryDITest() throws Exception {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(ChinaUserFactory.class);
        bd.setFactoryMethodName("getChinaUser");
        List<Object> args = Arrays.asList("静态工厂方式DI", new BeanReference("staticFactoryGarfieldCat"));
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("staticFactoryChinaUser", bd);

        bd = new GenericBeanDefinition();
        bd.setBeanClass(GarfieldCat.class);
        args = Arrays.asList("罐头");
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("staticFactoryGarfieldCat", bd);

        bf.preInstantiateSingletons();

        ChinaUser chinaUser = (ChinaUser) bf.getBean("staticFactoryChinaUser");
        System.out.println(chinaUser.toString());
    }

    @Test
    public void createBeanByBeanFactoryTest() throws Exception {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(ChinaUserFactoryBean.class);
        bf.registryBeanDefinition("chinaUserFactoryBean", bd);

        bd = new GenericBeanDefinition();
        bd.setFactoryBeanName("chinaUserFactoryBean");
        bd.setFactoryMethodName("getChinaUser");
        List<Object> args = Arrays.asList("工厂方法方式DI", new BeanReference("factoryBeanGarfieldCat"));
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("factoryBeanChinaUser", bd);

        bd = new GenericBeanDefinition();
        bd.setBeanClass(GarfieldCat.class);
        args = Arrays.asList("罐头");
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("factoryBeanGarfieldCat", bd);

        bf.preInstantiateSingletons();

        ChinaUser chinaUser = (ChinaUser) bf.getBean("factoryBeanChinaUser");
        System.out.println(chinaUser.toString());
    }

    @Test
    public void circulationDITest() throws Exception {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(ChinaUser.class);
        List<Object> args = Arrays.asList("构造方式DI", new BeanReference("garfieldCat"));
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("chinaUser", bd);

        bd = new GenericBeanDefinition();
        bd.setBeanClass(GarfieldCat.class);
        args = Arrays.asList("罐头", new BeanReference("chinaUser"));
        bd.setConstructorArgumentValues(args);
        bf.registryBeanDefinition("garfieldCat", bd);

        bf.preInstantiateSingletons();

        ChinaUser chinaUser = (ChinaUser) bf.getBean("chinaUser");
        System.out.println(chinaUser.toString());
    }

    @Test
    public void propertyCirculationDITest() throws Exception {
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

        bf.preInstantiateSingletons();

        ChinaUser chinaUser = (ChinaUser) bf.getBean("chinaUser");
        System.out.println(chinaUser.toString());

        GarfieldCat garfieldCat = (GarfieldCat) bf.getBean("garfieldCat");
        System.out.println(garfieldCat.toString());
    }
}
