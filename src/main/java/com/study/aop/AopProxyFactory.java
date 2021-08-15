package com.study.aop;

import com.study.aop.advisor.Advisor;
import com.study.beans.BeanFactory;

import java.util.List;

public interface AopProxyFactory {

    /**
     * @param bean 需要增强的对象,单理bean可以通过beanFactory获取,多例bean则不能,所以这里需要传递bean
     * @param beanName bean名称,可以通过bean名称获取bean定义或bean的其它信息
     * @param advisors 切面列表
     * @param beanFactory bean工厂
     */
    AopProxy createAopProxy(Object bean, String beanName, List<Advisor> advisors, BeanFactory beanFactory)
        throws Exception;

    /**
     * 获得默认的AopProxyFactory实例
     */
    static AopProxyFactory getDefaultAopProxyFactory() {
        return new DefaultAopProxyFactory();
    }
}
