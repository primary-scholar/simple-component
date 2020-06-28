package com.mimu.simple.zkreference;

import com.mimu.simple.zkreference.interceptor.ZKPropertyAttribute;

import java.lang.reflect.AnnotatedElement;

/**
 author: mimu
 date: 2020/4/29
 */
public interface ZKPropertyReferenceParser {

    ZKPropertyAttribute parseZKReferenceAnnotation(AnnotatedElement element);
}
