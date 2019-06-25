package com.mimu.simple.config;

import com.sun.org.apache.regexp.internal.RE;

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

    public static int tcp_read_idle_duration() {
        return getInt(SimpleServerConfigs.TCP_IDLE_READ, SimpleServerConfigs.TCP_IDLE_READ_DURATION);
    }

    public static int tcp_write_idle_duration() {
        return getInt(SimpleServerConfigs.TCP_IDLE_WRITE, SimpleServerConfigs.TCP_IDLE_WRITE_DURATION);
    }

    public static int tcp_idle_times() {
        return getInt(SimpleServerConfigs.TCP_IDLE_COUNT, SimpleServerConfigs.TCP_IDLE_TIMES);
    }

    private static boolean getBool(String key, String defaultValue) {
        return Boolean.parseBoolean(System.getProperty(key, defaultValue));
    }

    private static int getInt(String key, String defaultValue) {
        return Integer.parseInt(System.getProperty(key, defaultValue));
    }
}
