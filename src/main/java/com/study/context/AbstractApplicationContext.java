package com.study.context;

import com.study.beans.BeanFactory;
import com.study.beans.BeanPostProcessor;
import com.study.beans.DefaultBeanFactory;
import com.study.beans.PreBuildBeanFactory;

import java.io.IOException;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected BeanFactory beanFactory;

    public AbstractApplicationContext() {
        this.beanFactory = new PreBuildBeanFactory();
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    @Override
    public void registerBeanPostProcessor(BeanPostProcessor bpp) {
        beanFactory.registerBeanPostProcessor(bpp);
    }

    @Override
    public void close() {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                if (beanFactory instanceof DefaultBeanFactory) {
                    ((DefaultBeanFactory) beanFactory).close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

    }
}
