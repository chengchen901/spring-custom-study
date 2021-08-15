package com.study.context;

public interface BeanDefinitionReader {

    void loadBeanDefinitions(Resource resource) throws Exception;

    void loadBeanDefinitions(Resource... resource) throws Exception;
}
