package com.mimu.simple.core;


import com.mimu.simple.core.annotation.SimpleController;
import com.mimu.simple.core.annotation.SimpleRequestUrl;
import com.mimu.simple.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * author: mimu
 * date: 2018/10/22
 */
public class ControllerDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ControllerDispatcher.class);
    private Map<String, SimpleHandler> handlerMap;
    private AnnotationConfigApplicationContext context;

    public ControllerDispatcher(List<String> packages, boolean supportSpring) {
        handlerMap = supportSpring ? getHandlerWithSpring(packages) : getHandlerByScanPackage(packages);
    }

    public SimpleHandler getHandler(String url) {
        return handlerMap.get(url);
    }

    private Map<String, SimpleHandler> getHandlerWithSpring(List<String> packages) {
        Map<String, SimpleHandler> handlerMap = new HashMap<>();
        if (context == null) {
            context = new AnnotationConfigApplicationContext(packages.toArray(new String[]{}));
        }
        Map<String, Object> controller = context.getBeansWithAnnotation(SimpleController.class);
        Iterator<Map.Entry<String, Object>> iterator = controller.entrySet().iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next().getValue();
            Method[] methods = object.getClass().getDeclaredMethods();
            getHandler(handlerMap, object, methods);
        }
        return handlerMap;
    }


    /**
     * here if we didn't use spring to manage our bean
     * there a lot of things need to do ,such as dependencies inject , inversion of control
     * so lots of remaining things to be done while we haven't resolve it.
     *
     * @param packages
     * @return
     */
    private Map<String, SimpleHandler> getHandlerByScanPackage(List<String> packages) {
        Map<String, SimpleHandler> handlerMap = new HashMap<>();
        Set<Class<?>> classSet = ClassUtil.getClasses(packages);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(SimpleController.class)) {
                try {
                    Object object = clazz.newInstance();
                    Method[] methods = clazz.getDeclaredMethods();
                    getHandler(handlerMap, object, methods);
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("ControllerDispatcher resolve Handler error", e);
                }
            }
        }
        return handlerMap;
    }

    private void getHandler(Map<String, SimpleHandler> handlerMap, Object object, Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(SimpleRequestUrl.class)) {
                //String request  = method.getDeclaredAnnotation(SimpleRequestUrl.class).value();
                /*
                  here we use spring AnnotationUtils get the alias field value in a annotation class
                 */
                String request = AnnotationUtils.getAnnotation(method, SimpleRequestUrl.class).value();
                SimpleHandler handler = new SimpleHandler();
                handler.setObject(object);
                handler.setMethod(method);
                handlerMap.put(request, handler);
            }
        }
    }


}
