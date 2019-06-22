package com.mimu.simple.config;

/**
 * author: mimu
 * date: 2019/5/28
 */
public class SimpleServerConfigManager {

    public static boolean tcp_idle_switch() {
        return getBool(SimpleServerConfigs.TCP_IDLE_SWITCH, SimpleServerConfigs.TCP_IDLE_SWITCH_DEFAULT);
    }

    public static boolean fork_join_pool_switch() {
        return getBool(SimpleServerConfigs.FORK_JOIN_POOL_SWITCH, SimpleServerConfigs.FORK_JOIN_POOL_DEFAULT_DEFAULT);
    }

    private static boolean getBool(String key, String defaultValue) {
        return Boolean.parseBoolean(System.getProperty(key, defaultValue));
    }
}
