package com.study.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PreBuildBeanFactory extends DefaultBeanFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PreBuildBeanFactory.class);

    private final List<String> beanNames = new ArrayList<>();

    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionRegisterException {
        super.registryBeanDefinition(beanName, beanDefinition);
        synchronized (beanNames) {
            beanNames.add(beanName);
        }
    }

    public void preInstantiateSingletons() throws Exception {
        synchronized (beanNames) {
            for (String name : beanNames) {
                BeanDefinition bd = this.getBeanDefinition(name);
                if (bd.isSingleton()) {
                    this.doGetBean(name);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("preInstantiate: name=" + name + " " + bd);
                    }
                }
            }
        }
    }
}
