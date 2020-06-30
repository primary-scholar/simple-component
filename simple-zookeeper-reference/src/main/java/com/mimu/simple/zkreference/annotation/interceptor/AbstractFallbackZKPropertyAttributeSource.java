package com.mimu.simple.zkreference.annotation.interceptor;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 author: mimu
 date: 2020/4/29
 */
public abstract class AbstractFallbackZKPropertyAttributeSource implements ZKPropertyAttributeSource {

    private static final ZKPropertyAttribute NULL_ZKPROPERTY_ATTRIBUTE = new DefaultZKPropertyAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };

    private final Map<Object, ZKPropertyAttribute> propertyCache = new ConcurrentHashMap<>(1024);

    @Override
    public ZKPropertyAttribute getProperAttribute(Method method, Class<?> clazz) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object cacheKey = getCacheKey(method, clazz);
        ZKPropertyAttribute cached = this.propertyCache.get(cacheKey);
        if (cached != null) {
            if (cached == NULL_ZKPROPERTY_ATTRIBUTE) {
                return null;
            } else {
                return cached;
            }
        } else {
            ZKPropertyAttribute attribute = computeZKPropertyAttribute(method, clazz);
            if (attribute == null) {
                this.propertyCache.put(cacheKey, NULL_ZKPROPERTY_ATTRIBUTE);
            } else {
                this.propertyCache.put(cacheKey, attribute);
            }
            return attribute;
        }
    }

    protected ZKPropertyAttribute computeZKPropertyAttribute(Method method, Class<?> targetClass) {
        if (isPublicMethodOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method mostSpecificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        ZKPropertyAttribute zkPropertyAttribute = findZKPropertyAttribute(mostSpecificMethod);
        if (zkPropertyAttribute != null) {
            return zkPropertyAttribute;
        }
        return null;

    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    protected boolean isPublicMethodOnly() {
        return false;
    }

    protected abstract ZKPropertyAttribute findZKPropertyAttribute(Method method);
}
