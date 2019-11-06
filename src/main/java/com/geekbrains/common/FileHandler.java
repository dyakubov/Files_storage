package com.geekbrains.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;

public class FileHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServiceMessage serviceMessage = (ServiceMessage) msg;
        System.out.println("type: " + serviceMessage.getType());
        System.out.println("data:" + Arrays.asList(serviceMessage.getData()));


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
