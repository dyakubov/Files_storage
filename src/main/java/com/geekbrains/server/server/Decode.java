package com.geekbrains.server.server;

import com.geekbrains.common.AuthMessage;
import com.geekbrains.common.Commands;
import com.geekbrains.common.ServiceMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;

public class Decode extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s = (String)msg;
        String[] parts = s.split(Commands.SEPARATOR);
        if (parts[0].equals(Commands.START_IDENT) && parts[parts.length-1].equals(Commands.END_IDENT)){
            if (parts[1].equals(Commands.MessageType.AUTH)) {
                ctx.fireChannelRead(new AuthMessage(parts[2], parts[3]));
            } else ctx.fireChannelRead(new ServiceMessage(parts[1], Arrays.copyOfRange(parts, 2, parts.length-1)));
        } else ctx.writeAndFlush("Unknown protocol: " + s);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
