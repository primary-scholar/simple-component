package com.mimu.simple.httpserver.core.handler;

import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.httpserver.config.SimpleServerConfigManager;
import com.mimu.simple.httpserver.core.request.SimpleHttpRequest;
import com.mimu.simple.httpserver.core.response.SimpleHttpResponse;
import io.netty.buffer.ByteBuf;
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
 * date: 2019/6/24
 */
public class ActionHandler {

    private static final Logger serverLogger = LoggerFactory.getLogger("serverLogger");
    private Object object;
    private Method method;

    public Object getObject() {
        return object;
    }

    void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    Object execute(SimpleHttpRequest request, SimpleHttpResponse response) throws InvocationTargetException, IllegalAccessException {
        this.method.setAccessible(true);
        return this.method.invoke(object, request, response);
    }

    public void invoke(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            Object object = execute(request, response);
            if (object instanceof String) {
                response.response((String) object);
            } else {
                response.response(object);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            response.response(e.getStackTrace());
        }
    }

    public void execute(ChannelHandlerContext context, SimpleHttpRequest request, SimpleHttpResponse response, String id, long startTime) {
        /*
        异步执行业务逻辑，把业务逻辑放到 forkjoinpool 中执行
        */
        if (SimpleServerConfigManager.fork_join_pool_switch()) {
            CompletableFuture.runAsync(() -> invoke(request, response))
                    .thenRun(() -> context.executor().execute(() -> writeMessage(context, request, response, id, startTime)))
                    .exceptionally(throwable -> {
                        context.executor().execute(() -> writeError(context, request, response, throwable, id, startTime));
                        context.close();
                        return null;
                    });
        } else {
            /*
            同步执行业务逻辑，在当前的nioeventloop中执行业务逻辑
            */
            invoke(request, response);
            writeMessage(context, request, response, id, startTime);
        }
    }

    private void writeMessage(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, String id, long startTime) {
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, id, startTime);
    }

    private void writeError(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, Throwable throwable, String id, long startTime) {
        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("msg", throwable);
        simpleHttpResponse.response(result);
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, id, startTime);
    }

    private void write(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, String id, long startTime) {
        simpleHttpResponse.getResponse().headers().setInt(HttpHeaderNames.CONTENT_LENGTH, simpleHttpResponse.getResponse().content().readableBytes());
        boolean keepAlive = isKeepAlive(simpleHttpRequest);
        ByteBuf byteBuf = simpleHttpResponse.getResponse().content();
        String result = new String(byteBuf.array(), byteBuf.readerIndex(), byteBuf.readableBytes());
        if (!keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener((ChannelFutureListener) future -> {
                future.channel().close();
                if (future.isSuccess()) {
                    serverLogger.info("server handle over id={},result={},cost={} ms", id, result, System.currentTimeMillis() - startTime);
                } else {
                    serverLogger.error("server handle error id={},result={},cost={} ms", id, result, System.currentTimeMillis() - startTime);
                }
            });
        }
        if (keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    serverLogger.info("server handle over id={},result={},cost={} ms", id, result, System.currentTimeMillis() - startTime);
                } else {
                    serverLogger.error("server handle error id={},result={},cost={} ms", id, result, System.currentTimeMillis() - startTime);
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
