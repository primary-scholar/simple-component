package com.mimu.simple;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleHttpClientTest {

    @Test
    public void get() {
        System.out.println(SimpleHttpClient.get("https://www.baidu.com/index.php?tn=monline_3_dg"));
    }
}