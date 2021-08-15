package com.study.aop.advice;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends Advice {

    void afterReturning(Object returnValue, Method method, Object[] args, Object target);
}
