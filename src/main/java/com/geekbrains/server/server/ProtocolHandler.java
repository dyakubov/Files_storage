package com.geekbrains.server.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.geekbrains.common.Commands.*;

public class ProtocolHandler extends ChannelInboundHandlerAdapter {

    public String compileMessage(String type, String data){
        String result = START_IDENT + SEPARATOR + type + SEPARATOR;
        if (data == null){
            return EMPTY;
        }

        String[] split = data.split(" ");

        for (String s : split) {
            result = result.concat(s).concat(SEPARATOR);

        }
        return result.concat(END_IDENT);
    }

   @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        String result = "";
        while (buf.isReadable()){
            result = result.concat(Character.toString((char)buf.readByte()));
        }
       System.out.println("Compiled result: " + result);
        ctx.fireChannelRead(result);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}