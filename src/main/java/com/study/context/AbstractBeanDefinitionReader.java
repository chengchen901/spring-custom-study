package com.study.context;

import com.study.beans.BeanDefinitionRegistry;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    protected BeanDefinitionRegistry registry;

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }
}
