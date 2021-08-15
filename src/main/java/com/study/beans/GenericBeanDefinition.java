package com.study.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String scope;
    private String factoryMethodName;
    private String factoryBeanName;
    private String initMethod;
    private String destroyMethod;
    private List<?> constructorArgumentValues;
    private Constructor<?> constructor;
    private Method method;
    private List<PropertyValue> propertyValues;
    private ThreadLocal<Object[]> realConstructorArgumentValues = new ThreadLocal<>();

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_SINGLETON.equals(scope);
    }

    @Override
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    @Override
    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    @Override
    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    @Override
    public String getInitMethod() {
        return initMethod;
    }

    @Override
    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    @Override
    public String getDestroyMethod() {
        return destroyMethod;
    }

    @Override
    public void setConstructorArgumentValues(List<?> constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    @Override
    public List<?> getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    @Override
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public void setFactoryMethod(Method method) {
        this.method = method;
    }

    @Override
    public Method getFactoryMethod() {
        return method;
    }

    @Override
    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    @Override
    public void setConstructorArgumentRealValues(Object[] values) {
        realConstructorArgumentValues.set(values);
    }

    @Override
    public Object[] getConstructorArgumentRealValues() {
        return realConstructorArgumentValues.get();
    }

    @Override
    public String toString() {
        return "GenericBeanDefinition{" +
                "beanClass=" + beanClass +
                ", scope='" + scope + '\'' +
                ", factoryMethodName='" + factoryMethodName + '\'' +
                ", factoryBeanName='" + factoryBeanName + '\'' +
                ", initMethod='" + initMethod + '\'' +
                ", destroyMethod='" + destroyMethod + '\'' +
                ", constructorArgumentValues=" + constructorArgumentValues +
                ", constructor=" + constructor +
                ", method=" + method +
                ", propertyValues=" + propertyValues +
                ", realConstructorArgumentValues=" + realConstructorArgumentValues +
                '}';
    }
}
