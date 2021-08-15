package com.study.beans;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface BeanDefinition {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void setBeanClass(Class<?> beanClass);

    Class<?> getBeanClass();

    void setScope(String scope);

    String getScope();

    boolean isSingleton();

    boolean isPrototype();

    void setFactoryMethodName(String factoryMethodName);

    String getFactoryMethodName();

    void setFactoryBeanName(String factoryBeanName);

    String getFactoryBeanName();

    void setInitMethod(String initMethod);

    String getInitMethod();

    void setDestroyMethod(String destroyMethod);

    String getDestroyMethod();

    default boolean validate() {
        // 没有beanClass信息，只能通过成员工厂创建
        if (getBeanClass() == null) {
            if (StringUtils.isBlank(getFactoryBeanName()) || StringUtils.isBlank(getFactoryMethodName())) {
                return false;
            }
        }

        // class存在的情况，还指定factoryBeanName，构建对象方式冲突
        if (getBeanClass() != null && StringUtils.isNotBlank(getFactoryBeanName())) {
            return false;
        }

        return true;
    }

    void setConstructorArgumentValues(List<?> constructorArgumentValues);

    List<?> getConstructorArgumentValues();

    void setConstructor(Constructor<?> constructor);

    Constructor<?> getConstructor();

    void setFactoryMethod(Method method);

    Method getFactoryMethod();

    void setPropertyValues(List<PropertyValue> propertyValues);

    List<PropertyValue> getPropertyValues();

    /**
     * 在构造方法创建对象时缓存构造参数真实值,在后面cglib创建代理对象时使用
     * 为什么不直接使用 List<?> getConstructorArgumentValues(),因为构造参数中可能在beanReference
     */
    void setConstructorArgumentRealValues(Object[] values);

    Object[] getConstructorArgumentRealValues();
}
