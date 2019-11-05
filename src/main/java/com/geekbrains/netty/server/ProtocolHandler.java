package com.geekbrains.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import static com.geekbrains.common.Commands.*;

public class ProtocolHandler extends ChannelInboundHandlerAdapter {

    private static String makeCommand(MessageType type, byte[] data){
        String result = START_IDENT + SEPARATOR + type;
        if (data.length == 0){
            return EMPTY;
        }
        for (byte b : data) {
            result = result.concat(String.valueOf(b)).concat(SEPARATOR);

        }
        return result.concat(END_IDENT);
    }


//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = ((ByteBuf) msg);
//
//        if (state == -1) {
//            byte firstByte = buf.readByte();
//            type = DataType.getDataTypeFromByte(firstByte);
//            state = 0;
//            reqLen = 4;
//            System.out.println(type);
//        }
//
//        if (state == 0) {
//            if (buf.readableBytes() < reqLen) {
//                return;
//            }
//            reqLen = buf.readInt();
//            state = 1;
//            System.out.println("text size: " + reqLen);
//        }
//
//        if (state == 1) {
//            if (buf.readableBytes() < reqLen) {
//                return;
//            }
//            byte[] data = new byte[reqLen];
//            buf.readBytes(data);
//            String str = new String(data);
//            System.out.println(type + " " + str);
//            state = -1;
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}