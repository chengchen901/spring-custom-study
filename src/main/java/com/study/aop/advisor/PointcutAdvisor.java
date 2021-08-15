package com.study.aop.advisor;

import com.study.aop.pointcut.Pointcut;

public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();
}
