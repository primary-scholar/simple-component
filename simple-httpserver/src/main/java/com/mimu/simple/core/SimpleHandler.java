package com.mimu.simple.core;


import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.config.SimpleServerConfigManager;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * author: mimu
 * date: 2018/10/21
 */
public class SimpleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHandler.class);
    private Object object;
    private Method method;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private Object invoke(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            this.method.setAccessible(true);
            return this.method.invoke(object, request, response);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("SimpleHandler execute error", e);
        }
        return null;
    }

    public void execute(ChannelHandlerContext context, SimpleHttpRequest request, SimpleHttpResponse response, long startTime) {
        /*
        异步执行业务逻辑，把业务逻辑放到 forkjoinpool 中执行
        */
        if (SimpleServerConfigManager.fork_join_pool_switch()) {
            CompletableFuture.runAsync(() -> invoke(request, response))
                    .thenRun(() -> context.executor().execute(() -> writeMessage(context, request, response, startTime)))
                    .exceptionally(throwable -> {
                        writeError(context, request, response, throwable, startTime);
                        context.close();
                        return null;
                    });
        } else {
            /*
            同步执行业务逻辑，在当前的nioeventloop中执行业务逻辑
            */
            invoke(request, response);
            writeMessage(context, request, response, startTime);
        }
    }

    private void writeMessage(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, long startTime) {
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, startTime);
    }

    private void writeError(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, Throwable throwable, long startTime) {
        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("msg", throwable);
        simpleHttpResponse.response(result);
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, startTime);
    }

    private void write(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, long startTime) {
        simpleHttpResponse.getResponse().headers().setInt(HttpHeaderNames.CONTENT_LENGTH, simpleHttpResponse.getResponse().content().readableBytes());
        boolean keepAlive = isKeepAlive(simpleHttpRequest);
        if (!keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener((ChannelFutureListener) future -> {
                future.channel().close();
                if (future.isSuccess()) {
                    LOGGER.info("server handle over url={},cost={} ms", simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                }
            });
        }
        if (keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    LOGGER.info("server handle over url={},cost={} ms", simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                }
            });
        }
    }

    private boolean isKeepAlive(SimpleHttpRequest request) {
        CharSequence connection = request.getRequest().headers().get(HttpHeaderNames.CONNECTION);
        if (HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection)) {
            return false;
        }
        if (request.getRequest().protocolVersion().isKeepAliveDefault()) {
            return !HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection);
        } else {
            return HttpHeaderValues.KEEP_ALIVE.contentEqualsIgnoreCase(connection);
        }
    }
}
