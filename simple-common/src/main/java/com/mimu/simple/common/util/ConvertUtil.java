package com.mimu.simple.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class ConvertUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);
    private static Map<Class<?>, Map<PropertyDescriptor, Method>> readPropertyDescriptorMap = new ConcurrentHashMap<>();

    public static Map<String, Object> convert2Map(Object object) throws Exception {
        Map<String, Object> aNewMap = new HashMap<>();
        if (object == null) {
            return aNewMap;
        }
        initReadPropertyDescriptorMap(object.getClass());
        Map<PropertyDescriptor, Method> propertyDescriptorMethodMap = readPropertyDescriptorMap.get(object.getClass());
        propertyDescriptorMethodMap.forEach((descriptor, method) -> {
            String name = descriptor.getName();
            try {
                if (!name.equalsIgnoreCase("class")) {
                    Object value = method.invoke(object);
                    if (value != null) {
                        aNewMap.put(name, value);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("convert2Map error", e);
            }
        });
        return aNewMap;
    }

    private static void initReadPropertyDescriptorMap(Class<?> clazz) {
        Map<PropertyDescriptor, Method> propertyDescriptorMethodMap = readPropertyDescriptorMap.get(clazz);
        if (propertyDescriptorMethodMap == null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                Map<PropertyDescriptor, Method> readMethodMap = new HashMap<>();
                for (PropertyDescriptor descriptor : descriptors) {
                    Method readMethod = descriptor.getReadMethod();
                    if (readMethod != null) {
                        readMethodMap.put(descriptor, readMethod);
                    }
                }
                readPropertyDescriptorMap.put(clazz, readMethodMap);
            } catch (Exception e) {
                logger.error("initWritePropertyDescriptorMap error", e);
            }
        }
    }

}
