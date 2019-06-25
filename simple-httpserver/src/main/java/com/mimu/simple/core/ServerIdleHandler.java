package com.mimu.simple.core;

import com.mimu.simple.config.SimpleServerConfigManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: mimu
 * date: 2018/11/30
 */
public class ServerIdleHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerIdleHandler.class);
    private int idleTimes = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        idleTimes = 0;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE)) {
                if (++idleTimes >= SimpleServerConfigManager.tcp_idle_times()) {
                    try {
                        logger.warn("close idle connection from server,address={}", ctx.channel().remoteAddress());
                        ctx.close();
                    } catch (Exception e) {
                        logger.error("close idle connection from server error address={}", ctx.channel().remoteAddress(), e);
                    }
                } else {
                    logger.warn("connection lost heartbeat, address={}", ctx.channel().remoteAddress());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
