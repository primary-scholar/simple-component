package com.mimu.simple.httpserver.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: mimu
 * date: 2018/10/21
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SimpleRequestUrl {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";
}
