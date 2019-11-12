//package com.geekbrains.server;
//
//import com.geekbrains.common.messages.client.AuthMessage;
//import com.geekbrains.common.messages.client.RegistrationRequest;
//import com.geekbrains.common.messages.server.ServerMessage;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.util.ReferenceCountUtil;
//
//public class AuthHandler extends ChannelInboundHandlerAdapter {
//    private Users users = new Users();
//
//    private AuthInterface authInterface = new JDBCAuth();
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("Client connected...");
//
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        try {
//            if (msg == null)
//                return;
//            if (msg instanceof AuthMessage) {
//                AuthMessage message = (AuthMessage) msg;
//                System.out.println("Client auth message: " + message.getLogin() + " " + message.getPassword());
//                ServerMessage serverMessage = authInterface.checkAuth(message.getLogin(), message.getPassword());
//                ctx.write(serverMessage);
//
//            } else if (msg instanceof RegistrationRequest){
//                RegistrationRequest registrationRequest = (RegistrationRequest) msg;
//                System.out.println("Client reg request: " + registrationRequest.getLogin());
//                ServerMessage serverMessage = authInterface.regUser(registrationRequest.getLogin(), registrationRequest.getPassword());
//                ctx.write(serverMessage);
//
//            } else {
//                ctx.fireChannelRead(msg);
//            }
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
//}
