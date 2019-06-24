package com.mimu.simple.util;

import com.mimu.simple.core.FileItem;
import com.mimu.simple.core.SimpleHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class ConvertUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);
    private static Map<Class<?>, Map<PropertyDescriptor, Method>> writePropertyDescriptorMap = new ConcurrentHashMap<>();
    private static Map<Class<?>, Map<PropertyDescriptor, Method>> readPropertyDescriptorMap = new ConcurrentHashMap<>();

    public static <T> T convert(SimpleHttpRequest request, Class<T> clazz) throws Exception {
        initWritePropertyDescriptorMap(clazz);
        T target = clazz.newInstance();
        Map<PropertyDescriptor, Method> propertyDescriptorMethodMap = writePropertyDescriptorMap.get(clazz);
        propertyDescriptorMethodMap.forEach((descriptor, method) -> {
            String name = descriptor.getName();
            String typeName = descriptor.getPropertyType().getTypeName();
            if (typeName.equals(List.class.getName()) || typeName.equals(Map.class.getName())) {
                setFileCollectionField(descriptor, target, request);
            } else {
                String value = request.getString(name);
                if (StringUtils.isNotEmpty(value)) {
                    try {
                        setFieldValue(typeName, descriptor.getWriteMethod(), target, value);
                    } catch (Exception e) {
                        logger.error("convert error", e);
                    }
                }
            }
        });
        return target;
    }

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
                    aNewMap.put(name, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("convert2Map error", e);
            }
        });
        return aNewMap;
    }

    private static void initWritePropertyDescriptorMap(Class<?> clazz) {
        Map<PropertyDescriptor, Method> writePropertyDescriptorMethodMap = writePropertyDescriptorMap.get(clazz);
        if (writePropertyDescriptorMethodMap == null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                Map<PropertyDescriptor, Method> writeMethodMap = new HashMap<>();
                for (PropertyDescriptor descriptor : descriptors) {
                    Method writeMethod = descriptor.getWriteMethod();
                    if (writeMethod != null) {
                        writeMethod.setAccessible(true);
                        writeMethodMap.put(descriptor, writeMethod);
                    }
                }
                writePropertyDescriptorMap.put(clazz, writeMethodMap);
            } catch (Exception e) {
                logger.error("ConvertUtil initWritePropertyDescriptorMap error", e);
            }
        }
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
                logger.error("ConvertUtil initWritePropertyDescriptorMap error", e);
            }
        }
    }

    private static void setFieldValue(String typeName, Method method, Object source, Object target) throws Exception {
        if (method != null) {
            switch (typeName) {
                case "int":
                    method.invoke(source, Integer.parseInt(String.valueOf(target)));
                    break;
                case "long":
                    method.invoke(source, Long.parseLong(String.valueOf(target)));
                    break;
                case "float":
                    method.invoke(source, Float.parseFloat(String.valueOf(target)));
                    break;
                case "double":
                    method.invoke(source, Double.parseDouble(String.valueOf(target)));
                    break;
                case "short":
                    method.invoke(source, Short.parseShort(String.valueOf(target)));
                    break;
                default:
                    method.invoke(source, target);
            }
        }
    }

    private static void setFileCollectionField(PropertyDescriptor descriptor, Object object, SimpleHttpRequest request) {
        dealArgument(descriptor.getWriteMethod().getGenericParameterTypes(), descriptor, object, request);
    }

    private static void dealArgument(Type[] types, PropertyDescriptor descriptor, Object object, SimpleHttpRequest request) {
        if (types == null) {
            return;
        }
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                dealArgument(((ParameterizedType) type).getActualTypeArguments(), descriptor, object, request);
            } else if (type.getTypeName().equals(FileItem.class.getCanonicalName())) {
                try {
                    if (request.getFiles() != null && request.getFiles().keySet().size() > 0) {
                        if (descriptor.getPropertyType().getTypeName().equals(Map.class.getCanonicalName())) {
                            descriptor.getWriteMethod().invoke(object, request.getFiles());
                        } else if (descriptor.getPropertyType().getTypeName().equals(List.class.getCanonicalName())) {
                            Map<String, List<FileItem>> requestFile = request.getFiles();
                            Iterator<Map.Entry<String, List<FileItem>>> iterator = request.getFiles().entrySet().iterator();
                            if (iterator.hasNext()) {
                                List<FileItem> fileItems = requestFile.entrySet().iterator().next().getValue();
                                descriptor.getWriteMethod().invoke(object, fileItems);
                            }
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("ConvertUtil dealArgument error", e);
                }
            }
        }
    }
}
