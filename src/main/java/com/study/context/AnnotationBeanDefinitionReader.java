package com.study.context;

import com.study.beans.BeanDefinitionRegistry;
import com.study.beans.GenericBeanDefinition;
import com.study.context.config.annotation.Autowired;
import com.study.context.config.annotation.Component;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws Exception {
        loadBeanDefinitions(new Resource[]{resource});
    }

    @Override
    public void loadBeanDefinitions(Resource... resource) throws Exception {
        if (resource != null && resource.length > 0) {
            for (Resource r : resource) {
                retrieveAndRegisterBeanDefinition(r);
            }
        }
    }

    private void retrieveAndRegisterBeanDefinition(Resource r) throws Exception {
        if (r == null || r.getFile() == null) {
            return;
        }

        final File file = r.getFile();
        final String className = getClassNameFromFile(file);
        final Class<?> clazz = Class.forName(className);
        Component component = clazz.getAnnotation(Component.class);
        if (component == null) {
            return;
        }

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        bd.setScope(component.scope());
        bd.setFactoryMethodName(component.factoryMethodName());
        bd.setFactoryBeanName(component.factoryBeanName());
        bd.setInitMethod(component.initMethodName());
        bd.setDestroyMethod(component.destroyMethodName());

        // 获得所有构造方法，在构造方法上找@Autowired注解，如有，将这个构造方法set到bd;
        handlerConstructor(clazz, bd);

        // 处理工厂方法参数依赖
        if (StringUtils.isNotBlank(bd.getFactoryMethodName())) {
            handlerFactoryMethodArgs(clazz, bd);
        }
        // 处理属性依赖
        handlerPropertyDi(clazz, bd);

        String beanName = component.name();
        if ("".equals(beanName)) {
            // TODO 应用名称生成规则生成beanName
            // 默认驼峰命名
            final String simpleName = clazz.getSimpleName();
            beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
        // 注册bean定义
        registry.registryBeanDefinition(beanName, bd);
    }

    private void handlerPropertyDi(Class<?> clazz, GenericBeanDefinition bd) {
        // TODO
    }

    private void handlerFactoryMethodArgs(Class<?> clazz, GenericBeanDefinition bd) {
        // TODO
    }

    private void handlerConstructor(Class<?> clazz, GenericBeanDefinition bd) {
        final Constructor<?>[] cs = clazz.getConstructors();
        for (Constructor<?> c : cs) {
            if (c.getAnnotation(Autowired.class) != null) {
                bd.setConstructor(c);
                final Parameter[] ps = c.getParameters();
                // TODO 遍历获取参数上的注解，及创建参数依赖
                break;
            }
        }
    }
    private String str = AnnotationBeanDefinitionReader.class.getResource("/").toString().replace("file:/", "");

    private int classPathAbstractLength = AnnotationBeanDefinitionReader.class.getResource("/").toString().replace("file:/", "").length();

    private String getClassNameFromFile(File file) {
        final String absolutePath = file.getAbsolutePath();
        String name = absolutePath.substring(classPathAbstractLength, absolutePath.indexOf("."));
        return name.replace(File.separator, ".");
    }
}
