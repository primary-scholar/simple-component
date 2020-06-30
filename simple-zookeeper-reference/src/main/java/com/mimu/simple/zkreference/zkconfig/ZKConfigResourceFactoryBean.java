package com.mimu.simple.zkreference.zkconfig;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ZKConfigResourceFactoryBean implements FactoryBean<ZKConfigOperator>, InitializingBean {
    private ZKConfigResource zkConfigResource;
    private ZKConfigOperator zkConfigOperator;

    public ZKConfigResourceFactoryBean() {
    }

    public void setZkConfigResource(ZKConfigResource zkConfigResource) {
        this.zkConfigResource = zkConfigResource;
    }

    @Override
    public ZKConfigOperator getObject() throws Exception {
        if (zkConfigOperator == null) {
            afterPropertiesSet();
        }
        return zkConfigOperator;
    }

    @Override
    public Class<?> getObjectType() {

        return this.zkConfigOperator == null ? ZKConfigOperator.class : this.zkConfigOperator.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert this.zkConfigResource != null;
        this.zkConfigOperator = new ZKConfigOperator(zkConfigResource.getZkAddress(), zkConfigResource.getZkPath());
    }
}
