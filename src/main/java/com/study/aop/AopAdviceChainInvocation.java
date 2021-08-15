package com.study.aop;

import com.study.aop.advice.AfterReturningAdvice;
import com.study.aop.advice.MethodBeforeAdvice;
import com.study.aop.advice.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AOP通知链调用类
 */
public class AopAdviceChainInvocation {

    /** AOP调用链执行方法*/
    private static Method invokeMethod;
    static {
        try {
            invokeMethod = AopAdviceChainInvocation.class.getMethod("invoke");
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private Object proxy;
    private Object target;
    private Method method;
    private Object[] args;
    private List<Object> advisors;

    public AopAdviceChainInvocation(Object proxy, Object target, Method method, Object[] args, List<Object> advisors) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.args = args;
        this.advisors = advisors;
    }

    // 责任链执行记录索引号
    private int i = 0;

    public Object invoke() throws Throwable {
        if (i < advisors.size()) {
            final Object advisor = advisors.get(i++);
            if (advisor instanceof MethodBeforeAdvice) {
                // 执行前置增强
                ((MethodBeforeAdvice) advisor).before(method, args, target);
            } else if (advisor instanceof MethodInterceptor) {
                // 执行环绕增强和异常处理增强。注意这里给入的method 和 对象 是invoke方法和链对象
                return ((MethodInterceptor) advisor).invoke(invokeMethod, null, this);
            } else if (advisor instanceof AfterReturningAdvice) {
                // 当是后置增强时，先得得到结果，再执行后置增强逻辑
                Object returnValue = this.invoke();
                ((AfterReturningAdvice) advisor).afterReturning(returnValue, method, args, target);
                return returnValue;
            }
            // 回调，遍历完advices列表
            return invoke();
        } else {
            return method.invoke(target, args);
        }
    }
}
