package com.study.aop;

import com.study.aop.advisor.Advisor;
import com.study.aop.advisor.AdvisorRegistry;
import com.study.aop.advisor.PointcutAdvisor;
import com.study.aop.pointcut.Pointcut;
import com.study.beans.BeanFactory;
import com.study.beans.BeanFactoryAware;
import com.study.beans.BeanPostProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 功能增强的实现，用户和框架交互的核心类。<br/>
 * 用户：通过Advisor提供方面，像DefaultBeanFactory注入该实现<br/>
 * 框架内部：DefaultBeanFactory注入ioc容器，
 * DefaultBeanFactory调用BeanPostProcessor接口相关方法，
 * 进行功能增强。<br/>
 *
 * 实现：自动代理创建、通知者注册、Bean处理者、Bean工厂通知接口。
 */
public class AdvisorAutoProxyCreator implements BeanPostProcessor, AdvisorRegistry, BeanFactoryAware {

    private List<Advisor> advisors;
    private BeanFactory beanFactory;

    public AdvisorAutoProxyCreator() {
        this.advisors = new ArrayList<>();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        // 在此判断bean是否需要进行切面增强，及获得增强的通知实现
        List<Advisor> matchAdvisors = getMatchedAdvisors(bean, beanName);
        // 如需要就进行增强，再返回增强的对象。
        if (CollectionUtils.isNotEmpty(matchAdvisors)) {
            // 通过代理模式进行功能增强
            bean = this.createProxy(bean, beanName, matchAdvisors);
        }
        return bean;
    }

    private Object createProxy(Object bean, String beanName, List<Advisor> matchAdvisors) throws Exception {
        return AopProxyFactory.getDefaultAopProxyFactory()
                .createAopProxy(bean, beanName, matchAdvisors, beanFactory)
                .getProxy();
    }

    private List<Advisor> getMatchedAdvisors(Object bean, String beanName) {
        if (CollectionUtils.isEmpty(advisors)) {
            return null;
        }

        final Class<?> beanClass = bean.getClass();
        final List<Method> allMethods = getAllMethodForClass(beanClass);

        // 存放匹配的Advisor的list
        List<Advisor> matchAdvisors = new ArrayList<>();
        for (Advisor ad : advisors) {
            if (ad instanceof PointcutAdvisor) {
                if (isPointcutMatchBean((PointcutAdvisor) ad, beanClass, allMethods)) {
                    matchAdvisors.add(ad);
                }
            }
        }
        return matchAdvisors;
    }

    private boolean isPointcutMatchBean(PointcutAdvisor pa, Class<?> beanClass, List<Method> allMethods) {
        final Pointcut p = pa.getPointcut();

        // 首先判断类是否匹配
        if (!p.matchsClass(beanClass)) {
            return false;
        }

        // 再判断是否有方法匹配
        for (Method method : allMethods) {
            if (p.matchsMethod(method, beanClass)) {
                return true;
            }
        }

        return false;
    }

    private List<Method> getAllMethodForClass(Class<?> beanClass) {
        List<Method> allMethods = new LinkedList<>();
        Set<Class<?>> classes = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(beanClass));
        classes.add(beanClass);
        for (Class<?> clazz : classes) {
            // 通过spring framework提供的工具类找出所有方法，包括从父类继承而来的方法
            final Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            allMethods.addAll(Arrays.asList(methods));
        }
        return allMethods;
    }

    @Override
    public void registAdvisor(Advisor ad) {
        if (ad == null) {
            return;
        }
        advisors.add(ad);
    }

    @Override
    public List<Advisor> getAdvisors() {
        return advisors;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
