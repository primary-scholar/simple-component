package com.mimu.simple.zkreference.annotation;

import com.mimu.simple.zkreference.annotation.interceptor.AbstractFallbackZKPropertyAttributeSource;
import com.mimu.simple.zkreference.annotation.interceptor.ZKPropertyAttribute;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 author: mimu
 date: 2020/4/28
 */
public class AnnotationZkPropertyAttributeSource extends AbstractFallbackZKPropertyAttributeSource implements Serializable {

    private final boolean publicMethodOnly;
    private final Set<ZKPropertyReferenceParser> annotationParsers;

    public AnnotationZkPropertyAttributeSource() {
        this(true);
    }

    public AnnotationZkPropertyAttributeSource(boolean publicMethodOnly) {
        this.publicMethodOnly = publicMethodOnly;
        this.annotationParsers = new LinkedHashSet<>(1);
        this.annotationParsers.add(new DefaultZKPropertyReferenceParser());
    }

    @Override
    protected ZKPropertyAttribute findZKPropertyAttribute(Method method) {
        return determineZKPropertyAttribute(method);
    }

    protected ZKPropertyAttribute determineZKPropertyAttribute(AnnotatedElement element) {
        for (ZKPropertyReferenceParser parser : this.annotationParsers) {
            ZKPropertyAttribute attribute = parser.parseZKReferenceAnnotation(element);
            if (attribute != null) {
                return attribute;
            }
        }
        return null;
    }

    public boolean isPublicMethodOnly() {
        return publicMethodOnly;
    }

    @Override
    public int hashCode() {
        return this.annotationParsers.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AnnotationZkPropertyAttributeSource)) {
            return false;
        }
        AnnotationZkPropertyAttributeSource objTag = (AnnotationZkPropertyAttributeSource) obj;
        return (this.annotationParsers.equals(objTag.annotationParsers) && this.publicMethodOnly == objTag.publicMethodOnly);
    }
}
