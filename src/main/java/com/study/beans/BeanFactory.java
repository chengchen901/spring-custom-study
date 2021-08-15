package com.study.beans;

public interface BeanFactory {

    Object getBean(String beanName) throws Exception;

    void registerBeanPostProcessor(BeanPostProcessor bpp);
}
