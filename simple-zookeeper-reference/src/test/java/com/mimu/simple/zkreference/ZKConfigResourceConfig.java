package com.mimu.simple.zkreference;

import com.mimu.simple.zkreference.zkconfig.ZKConfigOperator;
import com.mimu.simple.zkreference.zkconfig.ZKConfigResource;
import com.mimu.simple.zkreference.zkconfig.ZKConfigResourceFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:app.properties")
public class ZKConfigResourceConfig {

    @Value("${zkconfig.address}")
    private String address;
    @Value("${zkconfig.path}")
    private String path;

    @Bean
    public ZKConfigResource zkConfigResource() {
        ZKConfigResource configResource = new ZKConfigResource();
        configResource.setZkAddress(address);
        configResource.setZkPath(path);
        return configResource;
    }

    @Bean
    public ZKConfigOperator zkConfigOperator(ZKConfigResource zkConfigResource) throws Exception {
        ZKConfigResourceFactoryBean factoryBean = new ZKConfigResourceFactoryBean();
        factoryBean.setZkConfigResource(zkConfigResource);
        return factoryBean.getObject();
    }
}
