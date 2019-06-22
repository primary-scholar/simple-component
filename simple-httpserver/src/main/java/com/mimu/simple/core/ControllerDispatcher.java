package com.mimu.simple.core;


import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.config.SimpleServerConfigManager;
import com.mimu.simple.core.annotation.SimpleController;
import com.mimu.simple.core.annotation.SimpleRequestUrl;
import com.mimu.simple.util.ClassUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * author: mimu
 * date: 2018/10/22
 */
public class ControllerDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerDispatcher.class);
    private Map<String, SimpleHandler> handlerMap;
    private AnnotationConfigApplicationContext context;

    public ControllerDispatcher(List<String> packages, boolean supportSpring) {
        handlerMap = supportSpring ? getHandlerWithSpring(packages) : getHandlerByScanPackage(packages);
    }

    public void execute(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest(channelHandlerContext.channel(), request);
        SimpleHttpResponse simpleHttpResponse = new SimpleHttpResponse(fullHttpResponse);
        String url = simpleHttpRequest.getUrl();
        SimpleHandler handler = handlerMap.get(url);
        if (handler == null) {
            LOGGER.info("ControllerDispatcher getHandler error url={}", url);
            simpleHttpResponse.response("the url has no dispatcher");
            return;
        }
        /*
        异步执行业务逻辑，把业务逻辑放到 forkjoinpool 中执行
        */
        if (SimpleServerConfigManager.fork_join_pool_switch()) {
            CompletableFuture.runAsync(() -> handler.execute(simpleHttpRequest, simpleHttpResponse))
                    .thenRun(() -> channelHandlerContext.executor().execute(() -> writeMessage(channelHandlerContext, simpleHttpRequest, simpleHttpResponse)))
                    .exceptionally(throwable -> {
                        writeError(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, throwable);
                        channelHandlerContext.close();
                        return null;
                    });
        } else {
            /*
            同步执行业务逻辑，在当前的nioeventloop中执行业务逻辑
            */
            handler.execute(simpleHttpRequest, simpleHttpResponse);
            writeMessage(channelHandlerContext, simpleHttpRequest, simpleHttpResponse);
        }
    }

    private Map<String, SimpleHandler> getHandlerWithSpring(List<String> packages) {
        Map<String, SimpleHandler> handlerMap = new HashMap<>();
        if (context == null) {
            context = new AnnotationConfigApplicationContext(packages.toArray(new String[]{}));
        }
        Map<String, Object> controller = context.getBeansWithAnnotation(SimpleController.class);
        Iterator<Map.Entry<String, Object>> iterator = controller.entrySet().iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next().getValue();
            Method[] methods = object.getClass().getDeclaredMethods();
            getHandler(handlerMap, object, methods);
        }
        return handlerMap;
    }


    /**
     * here if we didn't use spring to manage our bean
     * there a lot of things need to do ,such as dependencies inject , inversion of control
     * so lots of remaining things to be done while we haven't resolve it.
     *
     * @param packages
     * @return
     */
    private Map<String, SimpleHandler> getHandlerByScanPackage(List<String> packages) {
        Map<String, SimpleHandler> handlerMap = new HashMap<>();
        Set<Class<?>> classSet = ClassUtil.getClasses(packages);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(SimpleController.class)) {
                try {
                    Object object = clazz.newInstance();
                    Method[] methods = clazz.getDeclaredMethods();
                    getHandler(handlerMap, object, methods);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error("ControllerDispatcher resolve Handler error", e);
                }
            }
        }
        return handlerMap;
    }

    private void getHandler(Map<String, SimpleHandler> handlerMap, Object object, Method[] methods) {
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.isAnnotationPresent(SimpleRequestUrl.class)) {
                //String request  = method.getDeclaredAnnotation(SimpleRequestUrl.class).value();
                /*
                  here we use spring AnnotationUtils get the alias field value in a annotation class
                 */
                String request = AnnotationUtils.getAnnotation(method, SimpleRequestUrl.class).value();
                SimpleHandler handler = new SimpleHandler();
                handler.setObject(object);
                handler.setMethod(method);
                handlerMap.put(request, handler);
            }
        }
    }

    private void writeMessage(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse) {
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse);
    }

    private void writeError(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse, Throwable throwable) {
        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("msg", throwable);
        simpleHttpResponse.response(result);
        write(channelHandlerContext, simpleHttpRequest, simpleHttpResponse);
    }

    private void write(ChannelHandlerContext channelHandlerContext, SimpleHttpRequest simpleHttpRequest, SimpleHttpResponse simpleHttpResponse) {
        simpleHttpResponse.getResponse().headers().setInt(HttpHeaderNames.CONTENT_LENGTH, simpleHttpResponse.getResponse().content().readableBytes());
        boolean keepAlive = isKeepAlive(simpleHttpRequest);
        if (!keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener(ChannelFutureListener.CLOSE);
        }
        if (keepAlive) {
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse());
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
