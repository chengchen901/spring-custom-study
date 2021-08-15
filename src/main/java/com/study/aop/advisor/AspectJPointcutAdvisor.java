package com.study.aop.advisor;

import com.study.aop.pointcut.AspectJExpressionPointcut;
import com.study.aop.pointcut.Pointcut;

public class AspectJPointcutAdvisor implements PointcutAdvisor {

    /** 通知bean名称*/
    private String adviceBeanName;

    /** 切点表达式*/
    private String expression;

    /** aspectJ切点实例*/
    private Pointcut pointcut;

    public AspectJPointcutAdvisor(String adviceBeanName, String expression) {
        this.adviceBeanName = adviceBeanName;
        this.expression = expression;
        this.pointcut = new AspectJExpressionPointcut(this.expression);
    }

    @Override
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
