package com.mimu.simple;

import com.mimu.simple.config.SimpleServerConfigManager;
import com.mimu.simple.core.ControllerDispatcher;
import com.mimu.simple.core.HttpServerHandler;
import com.mimu.simple.core.ServerIdleHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * author: mimu
 * date: 2018/10/21
 */
public class SimpleHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServer.class);
    private static Builder builder = new Builder();
    private int contextLength;
    private int port;
    private List<String> packages;
    private boolean supportSpring;

    private SimpleHttpServer(int contextLength, int port, List<String> packages, boolean supportSpring) {
        this.contextLength = contextLength;
        this.port = port;
        this.packages = packages;
        this.supportSpring = supportSpring;
    }

    public void startServer() {
        //ControllerDispatcher controllerDispatcher = new ControllerDispatcher(packages, supportSpring);
        HttpServerHandler handler = new HttpServerHandler(new ControllerDispatcher(packages, supportSpring));
        EventLoopGroup connectionGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server.group(connectionGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline channelPipeline = channel.pipeline();
                        //channelPipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        channelPipeline.addLast(new HttpRequestDecoder());
                        /*
                          here we use HttpObjectAggregator to compose
                          HttpRequest/HttpResponse/HttpContent/LastHttpContent
                          to FullHttpReqeust or FullHttpResponse
                         */
                        channelPipeline.addLast(new HttpObjectAggregator(contextLength));
                        if (SimpleServerConfigManager.tcp_idle_switch()) {
                            channelPipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
                            channelPipeline.addLast(new ServerIdleHandler());
                        }
                        channelPipeline.addLast(new HttpResponseEncoder());
                        channelPipeline.addLast(new ChunkedWriteHandler());
                        channelPipeline.addLast(handler);
                    }
                });
        try {
            ChannelFuture channelFuture = server.bind(port).sync();
            LOGGER.info("server start at port {} ...", port);
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) future -> {
                connectionGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            connectionGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOGGER.info("server shutdown ...");
        }));
    }

    public static Builder getServer() {
        return builder;
    }

    public static class Builder {
        private int contextLength = 10 * 1024 * 1024;
        private int port = 8080;
        private List<String> packages = Collections.emptyList();
        private boolean supportSpring = true;

        public Builder contextLength(int contextLength) {
            this.contextLength = contextLength;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder packages(List<String> packages) {
            this.packages = packages;
            return this;
        }

        public Builder supportSpring(boolean supportSpring) {
            this.supportSpring = supportSpring;
            return this;
        }

        public SimpleHttpServer create() {
            return new SimpleHttpServer(contextLength, port, packages, supportSpring);
        }
    }
}
