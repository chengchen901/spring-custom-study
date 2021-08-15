package com.study.context.config.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER,
ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;
}
