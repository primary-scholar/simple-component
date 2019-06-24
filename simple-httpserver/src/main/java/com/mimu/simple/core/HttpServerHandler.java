package com.mimu.simple.core;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * author: mimu
 * date: 2018/10/22
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private ControllerDispatcher dispatcher;

    public HttpServerHandler(ControllerDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        long startTime = System.currentTimeMillis();
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
                    logger.error("server handle over url={},cost={} ms", simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                } else {
                    logger.error("server handle error url={},cost={} ms", simpleHttpRequest.getUrl(), System.currentTimeMillis() - startTime);
                }
            });
        } else {
            simpleHttpRequest.parseRequest();
            handler.execute(channelHandlerContext, simpleHttpRequest, simpleHttpResponse, startTime);
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
