package com.mimu.simple.zkreference;

import com.mimu.simple.zkreference.interceptor.DefaultZKPropertyAttribute;
import com.mimu.simple.zkreference.interceptor.ZKPropertyAttribute;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.reflect.AnnotatedElement;

/**
 * author: mimu
 * date: 2020/4/29
 */
public class DefaultZKPropertyReferenceParser implements ZKPropertyReferenceParser {
    @Override
    public ZKPropertyAttribute parseZKReferenceAnnotation(AnnotatedElement element) {
        AnnotationAttributes annotationAttributes = AnnotatedElementUtils.findMergedAnnotationAttributes(element, ZKReference.class, false, false);
        if (annotationAttributes != null) {
            return new DefaultZKPropertyAttribute(annotationAttributes.getString("key"), annotationAttributes.getString("value"));
        }
        return null;
    }

    @Override
    public int hashCode() {
        return DefaultZKPropertyReferenceParser.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this || obj instanceof DefaultZKPropertyReferenceParser);
    }
}
