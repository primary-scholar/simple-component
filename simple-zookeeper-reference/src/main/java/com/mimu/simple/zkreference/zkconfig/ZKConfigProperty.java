package com.mimu.simple.zkreference.zkconfig;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * author: mimu
 * date: 2019/12/11
 */
public class ZKConfigProperty {
    private static final Logger logger = LoggerFactory.getLogger(ZKConfigProperty.class);
    private static final ZKConfigResource zkConfigResource;

    static {
        zkConfigResource = ZKConfigProperty.build().zkAddress("localhost:2181").rootPath("/configuration").init();
    }

    public static Builder build() {
        return new Builder();
    }

    public static final class Builder {
        private String address;
        private String path;

        public Builder zkAddress(String zkAddress) {
            this.address = zkAddress;
            return this;
        }


        public Builder rootPath(String rootPath) {
            this.path = rootPath;
            return this;
        }

        public ZKConfigResource init() {
            return new ZKConfigResource(address, path);
        }
    }


    public static DynamicStringProperty getString(String key, String defaultValue) {
        return getString(key, defaultValue, null);
    }

    public static DynamicStringProperty getString(String key, String defaultValue, Runnable callable) {
        return DynamicPropertyFactory.getInstance().getStringProperty(key, defaultValue, callable);
    }

    /**
     * 获取 ZKConfigurationResource 中 rootPath 路径下的所有 数据
     *
     * @return
     */
    public static Map<String, String> getCurrentData() {
        return zkConfigResource.getCurrentData();
    }
}
