package com.mimu.simple.httpserver.core.handler;

import com.mimu.simple.httpserver.core.request.SimpleHttpRequest;
import com.mimu.simple.httpserver.core.response.SimpleHttpResponse;
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

    Object execute(SimpleHttpRequest request, SimpleHttpResponse response) throws InvocationTargetException, IllegalAccessException {
        this.method.setAccessible(true);
        return this.method.invoke(object, request, response);
    }

    public void invoke(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            Object object = execute(request, response);
            if (object instanceof String) {
                response.response((String) object);
            } else {
                response.response(object);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            response.response(e.getStackTrace());
        }
    }

}
