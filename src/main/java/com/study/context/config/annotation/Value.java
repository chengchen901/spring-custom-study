package com.study.context.config.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    String value();
}
