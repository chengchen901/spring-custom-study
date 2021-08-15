package com.study.aop;

import com.study.aop.advisor.Advisor;
import com.study.beans.BeanDefinition;
import com.study.beans.BeanFactory;
import com.study.beans.DefaultBeanFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class CglibDynamicAopProxy implements MethodInterceptor, AopProxy {

    private static final Logger LOG = LoggerFactory.getLogger(CglibDynamicAopProxy.class);

    private static Enhancer enhancer = new Enhancer();

    private String beanName;
    private Object bean;
    private List<Advisor> advisors;
    private BeanFactory beanFactory;
    public CglibDynamicAopProxy(String beanName, Object bean, List<Advisor> advisors, BeanFactory beanFactory) {
        this.beanName = beanName;
        this.bean = bean;
        this.advisors = advisors;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getProxy() {
        return getProxy(bean.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader cl) {
        LOG.debug("为" + bean + "创建cglib代理。");
        final Class<?> clazz = bean.getClass();
        enhancer.setSuperclass(clazz);
        enhancer.setInterfaces(clazz.getInterfaces());
        enhancer.setCallback(this);
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
        }

        if (constructor != null) {
            return enhancer.create();
        } else {
            final BeanDefinition bd = ((DefaultBeanFactory) beanFactory).getBeanDefinition(beanName);
            return enhancer.create(bd.getConstructor().getParameterTypes(), bd.getConstructorArgumentRealValues());
        }
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return AopProxyUtils.applyAdvices(bean, method, args, advisors, proxy, beanFactory);
    }
}
