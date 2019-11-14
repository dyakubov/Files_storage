package com.geekbrains.server.netty.handlers;

import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.geekbrains.common.Settings.SERVER_FOLDER;

public class ServicesHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServiceMessage){
            ServiceMessage sm = (ServiceMessage)msg;
            switch (sm.getClientMessageType()){
                case DELETE:
                    deleteFileFromServer(msg, ctx);
                    break;
                case GET:
                    ctx.fireChannelRead(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private void deleteFileFromServer(Object msg, ChannelHandlerContext ctx) throws IOException {
        DeleteRequest dr = (DeleteRequest)msg;
        if (Files.deleteIfExists(Paths.get(SERVER_FOLDER + dr.getFileName()))) {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.DELETED));
        } else {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }
    }
}
