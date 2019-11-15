package com.geekbrains.server.netty.handlers;

import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.client.ServiceMessageType;
import com.geekbrains.common.messages.server.ServerMessage;
import com.geekbrains.server.auth.AuthInterface;
import com.geekbrains.server.auth.TestAuthService;
import com.geekbrains.server.users.Users;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private Users users = new Users();

    private AuthInterface authInterface = new TestAuthService(users);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            if (msg instanceof ServiceMessage) {
                ServiceMessage message = (ServiceMessage) msg;
                if (message.getServiceMessageType().equals(ServiceMessageType.AUTH)) {
                    ServerMessage serverMessage = authInterface.checkAuth(message.getUser().getLogin(), message.getUser().getPass());
                    ctx.write(serverMessage);
                } else if (message.getServiceMessageType().equals(ServiceMessageType.REG)) {
                    ServerMessage serverMessage = authInterface.regUser(message.getUser().getLogin(), message.getUser().getPass());
                    ctx.write(serverMessage);
                } else ctx.fireChannelRead(msg);
            } else if (msg instanceof FileContainer){
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
}
