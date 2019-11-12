package com.geekbrains.server;

import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServicesHandler extends ChannelInboundHandlerAdapter {
    private final String serverFolder = ServerApp.getServerFolder();
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
        if (Files.deleteIfExists(Paths.get(serverFolder + dr.getFileName()))) {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.DELETED));
        } else {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }
    }
}
