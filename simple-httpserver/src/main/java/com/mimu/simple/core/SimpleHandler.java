package com.mimu.simple.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: mimu
 * date: 2018/10/21
 */
public class SimpleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHandler.class);
    private Object object;
    private Method method;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object execute(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            this.method.setAccessible(true);
            return this.method.invoke(object, request, response);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("SimpleHandler execute error", e);
        }
        return null;
    }
}
