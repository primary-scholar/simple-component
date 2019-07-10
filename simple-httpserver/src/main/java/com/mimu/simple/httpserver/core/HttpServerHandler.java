package com.mimu.simple.httpserver.core;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


/**
 * author: mimu
 * date: 2018/10/22
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final Logger serverLogger = LoggerFactory.getLogger("serverLogger");

    private ControllerDispatcher dispatcher;

    public HttpServerHandler(ControllerDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        long startTime = System.currentTimeMillis();
        String id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest(channelHandlerContext.channel(), request);
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        SimpleHttpResponse simpleHttpResponse = new SimpleHttpResponse(fullHttpResponse);
        SimpleHandler handler = dispatcher.getHandler(simpleHttpRequest.getUrl());
        if (handler == null) {
            Info info = new Info(404, "url is error");
            simpleHttpResponse.response(info);
            simpleHttpResponse.getResponse().headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            simpleHttpResponse.getResponse().headers().setInt(HttpHeaderNames.CONTENT_LENGTH, simpleHttpResponse.getResponse().content().readableBytes());
            channelHandlerContext.writeAndFlush(simpleHttpResponse.getResponse()).addListener((ChannelFutureListener) future -> {
                future.channel().close();
                if (future.isSuccess()) {
                    serverLogger.info("server handle over id={},url={},cost={} ms", id, simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                } else {
                    serverLogger.error("server handle error id={},url={},cost={} ms", id, simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                }
            });
        } else {
            simpleHttpRequest.parseRequest();
            serverLogger.info("server handler start id={},url={},header={},parameter={},files={}", id, simpleHttpRequest.getUrl(), simpleHttpRequest.getHeaders(), simpleHttpRequest.getParameters(), simpleHttpRequest.getFiles());
            handler.execute(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, id, startTime);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        logger.error("exceptionCaught error", cause);
        channelHandlerContext.close();
    }


}
