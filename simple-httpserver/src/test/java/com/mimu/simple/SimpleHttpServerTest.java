package com.mimu.simple;

import com.mimu.simple.config.AppConfig;
import com.mimu.simple.httpserver.SimpleHttpServer;
import org.junit.Test;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class SimpleHttpServerTest {

    @Test
    public void startServer() {
        SimpleHttpServer server = SimpleHttpServer.getServer().config(AppConfig.class).create();
        server.startServer();
    }

    @Test
    public void startServerAgain() {
        /**
         * thus if we didn't use spring to manage our bean
         * there a lot of things need to do ,such as dependencies inject , inversion of control
         * so here has lots of things to deal with it while we didn't resolve.
         */
        SimpleHttpServer server = SimpleHttpServer.getServer().config(AppConfig.class).create();
        server.startServer();
    }

    public static void main(String[] args) {
        SimpleHttpServer server = SimpleHttpServer.getServer().config(AppConfig.class).port(9091).create();
        server.startServer();
    }
}