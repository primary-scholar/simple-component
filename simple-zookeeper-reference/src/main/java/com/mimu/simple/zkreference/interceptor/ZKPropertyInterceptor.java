package com.mimu.simple.zkreference.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;

import java.io.IOException;
import java.io.Serializable;

/**
 author: mimu
 date: 2020/4/28
 */
public class ZKPropertyInterceptor extends ZKPropertyAspectSupport implements MethodInterceptor, Serializable {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        return invokeWithInterceptor(invocation.getMethod(), targetClass);
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        out.writeObject(getPropertyAttribute());
        out.writeObject(getBeanFactory());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setPropertyAttribute((ZKPropertyAttributeSource) in.readObject());
        setBeanFactory((BeanFactory) in.readObject());
    }

}
