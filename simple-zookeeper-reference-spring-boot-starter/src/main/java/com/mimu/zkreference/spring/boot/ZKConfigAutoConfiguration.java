package com.mimu.zkreference.spring.boot;

import com.mimu.simple.zkreference.annotation.interceptor.ZKPropertyAspectSupport;
import com.mimu.simple.zkreference.zkconfig.ZKConfigOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(ZKPropertyAspectSupport.class)
@EnableConfigurationProperties(ZKConfigProperties.class)
public class ZKConfigAutoConfiguration {

    @Bean
    public ZKConfigOperator zkConfigResource(ZKConfigProperties zkConfigProperties) {
        return new ZKConfigOperator(zkConfigProperties.getAddress(), zkConfigProperties.getPath());
    }
}
