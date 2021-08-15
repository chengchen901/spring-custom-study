package com.study.aop;

import com.study.aop.advisor.Advisor;
import com.study.beans.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class JdkDynamicAopProxy implements InvocationHandler, AopProxy {

    private static final Logger LOG = LoggerFactory.getLogger(JdkDynamicAopProxy.class);

    private String beanName;
    private Object bean;
    private List<Advisor> advisors;
    private BeanFactory beanFactory;
    public JdkDynamicAopProxy(String beanName, Object bean, List<Advisor> advisors, BeanFactory beanFactory) {
        this.beanName = beanName;
        this.bean = bean;
        this.advisors = advisors;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return AopProxyUtils.applyAdvices(bean, method, args, advisors, proxy, beanFactory);
    }

    @Override
    public Object getProxy() {
        return getProxy(bean.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader cl) {
        LOG.debug("为" + bean + "创建cglib代理。");
        return Proxy.newProxyInstance(cl, bean.getClass().getInterfaces(), this);
    }
}
