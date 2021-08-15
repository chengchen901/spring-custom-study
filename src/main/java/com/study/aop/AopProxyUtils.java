package com.study.aop;

import com.study.aop.advisor.Advisor;
import com.study.aop.advisor.PointcutAdvisor;
import com.study.beans.BeanFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * AOP代理工具类
 */
public class AopProxyUtils {

    /**
     * 对方法应用advices增强，获得最终返回结果。
     *
     * @param target        bean对象，需要被增强的对象
     * @param method        需要被增强的方法
     * @param args          增强方法的参数
     * @param matchAdvisors 匹配到的切面
     * @param proxy         bean对象功能增强后的代理对象
     * @param beanFactory   ioc容器
     * @return 方法增强后的返回结果
     */
    public static Object applyAdvices(Object target, Method method, Object[] args, List<Advisor> matchAdvisors,
                                      Object proxy, BeanFactory beanFactory) throws Throwable {
        // 1、获取要对当前方法进行增强的advice Bean列表
        List<Object> advisors = getShouldApplyAdvices(target.getClass(), method, matchAdvisors, beanFactory);
        // 2、如有增强的advice，责任链式增强执行
        if (CollectionUtils.isEmpty(advisors)) {
            return method.invoke(target, args);
        } else {
            // 责任链式执行增强
            AopAdviceChainInvocation chain = new AopAdviceChainInvocation(proxy, target, method, args, advisors);
            return chain.invoke();
        }
    }

    /**
     * 获取与方法匹配的切面的advices Bean对象列表
     *
     * @param beanClass
     * @param method
     * @param matchAdvisors
     * @param beanFactory
     * @return
     * @throws Exception
     */
    public static List<Object> getShouldApplyAdvices(Class<?> beanClass, Method method, List<Advisor> matchAdvisors,
                                                     BeanFactory beanFactory) throws Throwable {
        if (CollectionUtils.isEmpty(matchAdvisors)) {
            return null;
        }
        List<Object> advices = new ArrayList<>();
        for (Advisor ad : matchAdvisors) {
            if (ad instanceof PointcutAdvisor) {
                if (((PointcutAdvisor) ad).getPointcut().matchsMethod(method, beanClass)) {
                    advices.add(beanFactory.getBean(ad.getAdviceBeanName()));
                }
            }
        }

        return advices;
    }
}
