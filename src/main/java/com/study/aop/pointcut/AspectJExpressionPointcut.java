package com.study.aop.pointcut;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;

import java.lang.reflect.Method;

public class AspectJExpressionPointcut implements Pointcut {

    /** 先获得切点解析器*/
    private static PointcutParser pp = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

    private String expression;

    /** aspectj中的切点表达式实例pe*/
    private PointcutExpression pe;

    public AspectJExpressionPointcut(String expression) {
        this.expression = expression;
        pe = pp.parsePointcutExpression(expression);
    }

    @Override
    public boolean matchsClass(Class<?> targetClass) {
        return pe.couldMatchJoinPointsInType(targetClass);
    }

    @Override
    public boolean matchsMethod(Method method, Class<?> targetClass) {
        return pe.matchesMethodExecution(method).alwaysMatches();
    }
}
