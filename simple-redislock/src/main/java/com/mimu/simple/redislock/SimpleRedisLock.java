package com.mimu.simple.redislock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;

/**
 * author: mimu
 * date: 2019/3/21
 */
public class SimpleRedisLock {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRedisLock.class);
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String requestIdKey = "request_key";
    private static JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(60);
        jedisPoolConfig.setMaxWaitMillis(200);
        jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 1000);
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = getJedis();
        try {
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("tryGetDistributedLock error", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis = getJedis();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("releaseDistributedLock error", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public static String getRequestId() {
        Jedis jedis = getJedis();
        try {
            long id = jedis.incr(requestIdKey);
            return String.valueOf(id);
        } catch (Exception e) {
            logger.error("getRequestId error", e);
        } finally {
            jedis.close();
        }
        return null;
    }

}
