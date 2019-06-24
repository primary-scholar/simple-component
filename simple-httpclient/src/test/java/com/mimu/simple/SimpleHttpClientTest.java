package com.mimu.simple;

import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class SimpleHttpClientTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void get() {
        System.out.println(SimpleHttpClient.get("https://www.baidu.com/index.php?tn=monline_3_dg"));
    }

    @Test
    public void futureGet() throws InterruptedException, ExecutionException, TimeoutException {
        Future<String> future = SimpleHttpClient.futureGet("https://www.baidu.com/index.php?tn=monline_3_dg");
        System.out.println(future.get(500,TimeUnit.MILLISECONDS));
    }

}