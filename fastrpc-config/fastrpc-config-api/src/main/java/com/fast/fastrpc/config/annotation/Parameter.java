package com.fast.fastrpc.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yiji
 * @version : Parameter.java, v 0.1 2020-10-01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Parameter {

    String name() default "";

    boolean required() default false;

    boolean excluded() default false;
}
