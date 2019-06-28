package com.mimu.simple.httpserver.config;

/**
 * author: mimu
 * date: 2019/5/28
 */
public class SimpleServerConfigs {

    /**
     * TCP idle switch
     */
    public static final String TCP_IDLE_SWITCH = "server.tcp.heartbeat.switch";
    public static final String TCP_IDLE_SWITCH_DEFAULT = "true";
    public static final String TCP_IDLE_READ = "server.tcp.read.idle";
    public static final String TCP_IDLE_READ_DURATION = "1500";
    public static final String TCP_IDLE_WRITE = "server.tcp.write.idle";
    public static final String TCP_IDLE_WRITE_DURATION = "1500";
    public static final String TCP_IDLE_COUNT = "server.tcp.idle.times";
    public static final String TCP_IDLE_TIMES = "3";


    /**
     * fork join pool switch default is true
     * it mean netty use eventloop deal the channel
     * while use fork join pool deal the logic
     */
    public static final String FORK_JOIN_POOL_SWITCH = "server.fork.join.pool.switch";
    public static final String FORK_JOIN_POOL_DEFAULT_DEFAULT = "true";


}
