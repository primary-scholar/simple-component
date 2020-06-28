package com.mimu.simple.zkreference;

import java.lang.annotation.*;

/**
 * author: mimu
 * date: 2020/4/27
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZKReference {

    String key() default "";

    String value() default "";
}
