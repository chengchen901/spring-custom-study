package com.study.context.config.annotation;

import com.study.beans.BeanDefinition;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String value() default "";

    String name() default "";

    String scope() default BeanDefinition.SCOPE_SINGLETON;

    String factoryMethodName() default "";

    String factoryBeanName() default "";

    String initMethodName() default "";

    String destroyMethodName() default "";
}
