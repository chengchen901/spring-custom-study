package com.study.aop.advisor;

public interface Advisor {

    String getAdviceBeanName();

    String getExpression();
}
