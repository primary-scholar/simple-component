package com.mimu.simple;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * author: mimu
 * date: 2019/3/21
 */
public class RedisLockTest {

    Executor executor = Executors.newFixedThreadPool(2);
    String lockKey = "lockKey";

    @Test
    public void info() {
        String requestId = SimpleRedisLock.getRequestId();
        System.out.println("requestId: " + requestId);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                boolean result = SimpleRedisLock.tryGetDistributedLock(lockKey, requestId, 1);
                System.out.println("getLock: " + result);
            });
        }

        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                boolean result = SimpleRedisLock.releaseDistributedLock(lockKey, requestId);
                System.out.println("releaseLock: " + result);
            });
        }
    }

}