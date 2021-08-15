package com.study.beans;

import com.study.aop.AdvisorAutoProxyCreator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    private Set<String> buildingBeans = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegisterException {
        Objects.requireNonNull(beanName, "注册bean需要指定beanName");
        Objects.requireNonNull(beanDefinition, "注册bean需要指定beanDefinition");

        if (!beanDefinition.validate()) {
            throw new BeanDefinitionRegisterException("名字为[" + beanName + "]的bean定义不合法:" + beanDefinition);
        }

        if (beanDefinitionMap.containsKey(beanName)) {
            throw new BeanDefinitionRegisterException("名字为[" + beanName + "]的bean已存在:" + getBeanDefinition(beanName));
        }

        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        Object bean = doGetBean(beanName);

        // 将属性注入代码移除doGetBean函数,解决属性可以循环依赖问题,这里这么处理不严谨
        final BeanDefinition beanDefinition = getBeanDefinition(beanName);
        // 完成属性注入
        setPropertyDIValues(beanDefinition, bean);

        bean = applyPostProcessBeforeInitialization(bean, beanName);

        // 开始bean的生命周期
        if (StringUtils.isNotBlank(beanDefinition.getInitMethod())) {
            doInitMethod(bean, beanDefinition);
        }

        bean = applyPostProcessAfterInitialization(bean, beanName);

        return bean;
    }

    @Override
    public void registerBeanPostProcessor(BeanPostProcessor bpp) {
        beanPostProcessors.add(bpp);

        if (bpp instanceof AdvisorAutoProxyCreator) {
            ((AdvisorAutoProxyCreator) bpp).setBeanFactory(this);
        }
    }

    // 应用bean初始化前的处理
    private Object applyPostProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor bpp : this.beanPostProcessors) {
            bean = bpp.postProcessBeforeInitialization(bean, beanName);
        }
        return bean;
    }

    // 应用bean初始化后的处理
    private Object applyPostProcessAfterInitialization(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor bpp : this.beanPostProcessors) {
            bean = bpp.postProcessAfterInitialization(bean, beanName);
        }
        return bean;
    }

    protected Object doGetBean(String beanName) throws Exception {
        Objects.requireNonNull(beanName, "beanName不能为空");

        Object bean = beanMap.get(beanName);
        if (bean != null) {
            return bean;
        }

        // 构建方式有三种,构造函数、静态工厂、成员工厂
        final BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Objects.requireNonNull(beanDefinition, "找不到[" + beanName + "]的bean定义信息");

        // 记录正在创建的Bean
        Set<String> ingBeans = this.buildingBeans;
        if (ingBeans.contains(beanName)) {
            throw new Exception(beanName + "循环依赖!" + ingBeans);
        }
        ingBeans.add(beanName);

        if (beanDefinition.getBeanClass() == null) {
            // 成员工厂
            bean = createBeanByBeanFactory(beanDefinition);
        } else {
            if (StringUtils.isBlank(beanDefinition.getFactoryMethodName())) {
                // 构造函数
                bean = createBeanByConstructor(beanDefinition);
            } else {
                // 静态工厂
                bean = createBeanByStaticFactory(beanDefinition);
            }
        }

        // 实例创建完成后进行删除
        ingBeans.remove(beanName);

        // 对单例bean的处理
        if (beanDefinition.isSingleton()) {
            beanMap.put(beanName, bean);
        }
        return bean;
    }

    private void setPropertyDIValues(BeanDefinition beanDefinition, Object bean) throws Exception {
        if (CollectionUtils.isEmpty(beanDefinition.getPropertyValues())) {
            return;
        }
        for (PropertyValue pv : beanDefinition.getPropertyValues()) {
            if (StringUtils.isBlank(pv.getName())) {
                continue;
            }
            final Class<?> clazz = bean.getClass();
            final Field p = clazz.getDeclaredField(pv.getName());
            p.setAccessible(true);

            Object rv = pv.getValue();
            Object v = null;
            if (rv == null) {
                v = null;
            } else if (rv instanceof BeanReference) {
                v = this.doGetBean(((BeanReference) rv).getBeanName());
            } else if (rv instanceof Object[]) {
                // TODO 处理集合中的bean引用
            } else if (rv instanceof Collection) {
                // TODO 处理集合中的bean引用
            } else if (rv instanceof Properties) {
                // TODO 处理properties中的bean引用
            } else if (rv instanceof Map) {
                // TODO 处理Map中的bean引用
            } else {
                v = rv;
            }

            p.set(bean, v);
        }
    }

    private void doInitMethod(Object bean, BeanDefinition beanDefinition) throws Exception {
        final Class<?> clazz = bean.getClass();
        final Method method = clazz.getMethod(beanDefinition.getInitMethod());
        method.invoke(bean);
    }

    private Object createBeanByBeanFactory(BeanDefinition beanDefinition) throws Exception {
        final Object bean = getBean(beanDefinition.getFactoryBeanName());
        Object[] args = getRealValues(beanDefinition.getConstructorArgumentValues());
        final Method method = determineFactoryMethod(beanDefinition, args, bean.getClass());
        return method.invoke(bean, args);
    }

    private Object createBeanByStaticFactory(BeanDefinition beanDefinition) throws Exception {
        final Class<?> beanClass = beanDefinition.getBeanClass();
        Object[] args = getRealValues(beanDefinition.getConstructorArgumentValues());
        final Method method = determineFactoryMethod(beanDefinition, args, beanDefinition.getBeanClass());
        return method.invoke(beanClass, args);
    }

    private Method determineFactoryMethod(BeanDefinition beanDefinition, Object[] args, Class<?> type) throws Exception {
        // 对于原型bean,从第二次开始获取bean实例时,可以直接从第一次缓存中获取工厂方法
        if (beanDefinition.getFactoryMethod() != null) {
            return beanDefinition.getFactoryMethod();
        }

        if (type == null) {
            type = beanDefinition.getBeanClass();
        }

        if (args == null) {
            return type.getMethod(beanDefinition.getFactoryMethodName(), null);
        }

        // 根据参数类型获取构造方法
        Method method = null;
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        try {
            method = type.getMethod(beanDefinition.getFactoryMethodName(), paramTypes);
        } catch (Exception e) {
        }

        // 遍历所有方法,对形参类型与实参类型获取工厂方法
        if (method == null) {
            final Method[] methods = type.getMethods();
            outer:
            for (Method m : methods) {
                final Class<?>[] parameterTypes = m.getParameterTypes();
                if (parameterTypes.length != args.length) {
                    continue;
                }

                for (int i = 0; i < parameterTypes.length; i++) {
                    if (!parameterTypes[i].isAssignableFrom(paramTypes[i])) {
                        continue outer;
                    }
                }

                method = m;
                break;
            }
        }

        if (method != null) {
            if (beanDefinition.isPrototype()) {
                beanDefinition.setFactoryMethod(method);
            }
        } else {
            throw new Exception("找不到对应的工厂方法:" + beanDefinition);
        }

        return method;
    }

    private Object createBeanByConstructor(BeanDefinition beanDefinition) throws Exception {
        Object instance = null;
        if (CollectionUtils.isEmpty(beanDefinition.getConstructorArgumentValues())) {
            instance = beanDefinition.getBeanClass().getConstructor().newInstance();
        } else {
            Object[] args = getConstructorArgumentValues(beanDefinition);
            if (args == null) {
                instance = beanDefinition.getBeanClass().getConstructor().newInstance();
            } else {
                beanDefinition.setConstructorArgumentRealValues(args);
                final Constructor<?> constructor = determineConstructor(beanDefinition, args);
                // 缓存构造函数由determineConstructor 中移到了这里，无论原型否都缓存，因为后面AOP需要用
                beanDefinition.setConstructor(constructor);
                instance = constructor.newInstance(args);
            }
        }
        return instance;
    }

    private Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) throws Exception {
        // 对于原型bean,从第二次开始获取bean实例时,可以直接从第一次缓存中获取构造方法
        if (beanDefinition.getConstructor() != null) {
            return beanDefinition.getConstructor();
        }

        if (args == null) {
            return beanDefinition.getBeanClass().getConstructor();
        }

        // 根据参数类型获取构造方法
        Constructor<?> ct = null;
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        try {
            ct = beanDefinition.getBeanClass().getConstructor(paramTypes);
        } catch (Exception e) {
        }

        // 遍历所有构造方法,对形参类型与实参类型获取构造方法
        if (ct == null) {
            final Constructor<?>[] constructors = beanDefinition.getBeanClass().getConstructors();
            outer:
            for (Constructor<?> c : constructors) {
                final Class<?>[] parameterTypes = c.getParameterTypes();
                if (parameterTypes.length != args.length) {
                    continue;
                }

                for (int i = 0; i < parameterTypes.length; i++) {
                    if (!parameterTypes[i].isAssignableFrom(paramTypes[i])) {
                        continue outer;
                    }
                }

                ct = c;
                break;
            }
        }

        if (ct != null) {
            // 对于原型bean,可以缓存找到的构造方法，方便下次构造实例对象。在BeanDefinition中获取设置所用构造方法的方法。
            // 同时在上面增加从beanDefinition中获取的逻辑。
            /* 后面AOP中会使用到，所以不管是否原型bean都进行缓存
            if (beanDefinition.isPrototype()) {
                beanDefinition.setConstructor(ct);
            }*/
        } else {
            throw new Exception("找不到对应的构造方法:" + beanDefinition);
        }

        return ct;
    }

    private Object[] getConstructorArgumentValues(BeanDefinition beanDefinition) throws Exception {
        final List<?> args = beanDefinition.getConstructorArgumentValues();
        return getRealValues(args);
    }

    /**
     * 参数值解析
     */
    private Object[] getRealValues(List<?> args) throws Exception {
        if (CollectionUtils.isEmpty(args)) {
            return null;
        }

        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            Object rv = args.get(i);
            Object v = null;
            if (rv == null) {
                v = null;
            } else if (rv instanceof BeanReference) {
                v = this.doGetBean(((BeanReference) rv).getBeanName());
            } else if (rv instanceof Object[]) {
                // TODO 处理集合中的bean引用
            } else if (rv instanceof Collection) {
                // TODO 处理集合中的bean引用
            } else if (rv instanceof Properties) {
                // TODO 处理properties中的bean引用
            } else if (rv instanceof Map) {
                // TODO 处理Map中的bean引用
            } else {
                v = rv;
            }
            values[i] = v;

        }
        return values;
    }

    @Override
    public void close() throws IOException {
        // 针对单理bean执行销毁方法
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            final String beanName = entry.getKey();
            final BeanDefinition beanDefinition = entry.getValue();

            if (beanMap.containsKey(beanName) && StringUtils.isNotBlank(beanDefinition.getDestroyMethod())) {
                final Object bean = beanMap.get(beanName);
                if (bean == null) {
                    continue;
                }

                final Class<?> clazz = bean.getClass();
                try {
                    final Method destroyMethod = clazz.getMethod(beanDefinition.getDestroyMethod());
                    destroyMethod.invoke(bean);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
