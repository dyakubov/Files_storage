package com.geekbrains.server;

import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.AuthMessage;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private Users users = new Users();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            if (msg instanceof AuthMessage) {
                AuthMessage message = (AuthMessage) msg;
                System.out.println("Client auth message: " + message.getLogin() + " " + message.getPassword());
                ServerMessage serverMessage = checkAuth(message.getLogin(), message.getPassword());
                ctx.write(serverMessage);

            } else {
                ctx.fireChannelRead(msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private ServerMessage checkAuth(String login, String pass) {
        if (userExist(login)) {
            if (users.getAllUsers().get(login).equals(pass)) {
                System.out.println("Login OK for user: " + login);
                users.addToAuthorizedUsers(new User(login));
                return new ServerMessage(ServerAnswerType.AUTH_OK);
            } else {
                System.out.println("Wrong password for user:" + login);
                return new ServerMessage(ServerAnswerType.AUTH_FAILED);
            }
        } else {
            System.out.println("User " + login + " not found");
            return new ServerMessage(ServerAnswerType.USER_NOT_FOUND);
        }
    }

    private boolean userExist(String login){
        return users.getAllUsers().containsKey(login);
    }
}
