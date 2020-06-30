package com.mimu.simple.zkreference.annotation;

import com.mimu.simple.zkreference.annotation.interceptor.BeanFactoryZKPropertyAttributeSourceAdvisor;
import com.mimu.simple.zkreference.annotation.interceptor.ZKPropertyAttributeSource;
import com.mimu.simple.zkreference.annotation.interceptor.ZKPropertyInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * author: mimu
 * date: 2020/4/28
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyZKPropertyConfiguration extends AbstractPropertyConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryZKPropertyAttributeSourceAdvisor zkPropertyAttributeSourceAdvisor(ZKPropertyAttributeSource attributeSource, ZKPropertyInterceptor interceptor) {
        BeanFactoryZKPropertyAttributeSourceAdvisor advisor = new BeanFactoryZKPropertyAttributeSourceAdvisor();
        advisor.setZkPropertyAttributeSource(attributeSource);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ZKPropertyAttributeSource propertyAttributeSource() {
        return new AnnotationZkPropertyAttributeSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ZKPropertyInterceptor propertyInterceptor(ZKPropertyAttributeSource attributeSource) {
        ZKPropertyInterceptor interceptor = new ZKPropertyInterceptor();
        interceptor.setPropertyAttribute(attributeSource);
        return interceptor;
    }

}
