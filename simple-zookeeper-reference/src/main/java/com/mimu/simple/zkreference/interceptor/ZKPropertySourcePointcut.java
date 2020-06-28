package com.mimu.simple.zkreference.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 author: mimu
 date: 2020/4/28
 */
public abstract class ZKPropertySourcePointcut extends StaticMethodMatcherPointcut implements Serializable {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        ZKPropertyAttributeSource source = getPropertyAttributeSource();
        return (source != null && source.getProperAttribute(method, targetClass) != null);
    }

    protected abstract ZKPropertyAttributeSource getPropertyAttributeSource();
}
