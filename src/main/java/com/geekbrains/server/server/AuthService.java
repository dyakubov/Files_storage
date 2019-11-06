package com.geekbrains.server.server;

import com.geekbrains.common.AuthMessage;
import com.geekbrains.common.Commands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthService extends ChannelInboundHandlerAdapter {
    private Users users;

    public AuthService (Users users){
        this.users = users;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthMessage){
            AuthMessage authMessage = (AuthMessage)msg;
            if (checkAuth(authMessage.getLogin(), authMessage.getPassword())){
                ctx.writeAndFlush(Commands.AUTH_OK);
                System.out.println(Commands.AUTH_OK);
            } else {
                ctx.writeAndFlush(Commands.AUTH_FAIL);
                System.out.println(Commands.AUTH_FAIL);
            }
        } else ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public boolean checkAuth(String login, String password){
        return users.getUsers().get(login).equals(password);
    }
}
