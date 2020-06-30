package com.mimu.simple.zkreference.annotation.interceptor;

/**
 * author: mimu
 * date: 2020/4/29
 */
public class DefaultZKPropertyAttribute implements ZKPropertyAttribute {
    private String zkReference;
    private String zkValue;

    public DefaultZKPropertyAttribute() {
    }

    public DefaultZKPropertyAttribute(String key, String value) {
        this.zkReference = key;
        this.zkValue = value;
    }

    @Override
    public String getReference() {
        return zkReference;
    }

    @Override
    public String getValue() {
        return zkValue;
    }
}
