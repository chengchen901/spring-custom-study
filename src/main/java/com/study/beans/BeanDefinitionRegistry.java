package com.study.beans;

public interface BeanDefinitionRegistry {

    void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegisterException;

    BeanDefinition getBeanDefinition(String beanName);

    boolean containsBeanDefinition(String beanName);
}
