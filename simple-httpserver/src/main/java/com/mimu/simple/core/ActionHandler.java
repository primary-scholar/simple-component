package com.mimu.simple.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: mimu
 * date: 2019/6/24
 */
public class ActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ActionHandler.class);
    private Object object;
    private Method method;

    public Object getObject() {
        return object;
    }

    void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    Object invoke(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            this.method.setAccessible(true);
            return this.method.invoke(object, request, response);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ActionHandler execute error", e);
        }
        return null;
    }
}
