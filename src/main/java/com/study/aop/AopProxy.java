package com.study.aop;

public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader cl);
}
