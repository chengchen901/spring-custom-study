package com.study.aop.advice;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Advice {

    Object invoke(Method method, Object[] args, Object target) throws Throwable;
}
