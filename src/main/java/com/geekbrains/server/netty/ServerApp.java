package com.geekbrains.server.netty;

import com.geekbrains.common.Settings;
import com.geekbrains.server.netty.handlers.AuthHandler;
import com.geekbrains.server.netty.handlers.ServerFileHandler;
import com.geekbrains.server.security.Users;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import static com.geekbrains.common.Settings.PORT;

public class ServerApp {

    private static final int MAX_OBJ_SIZE = 1024 * 1024 * 100; // 10 mb

    private void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        new Users();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addFirst("decode", new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)));
                            socketChannel.pipeline().addLast("encode", new ObjectEncoder());
                            socketChannel.pipeline().addLast("auth", new AuthHandler());
                            socketChannel.pipeline().addLast("fileHandler", new ServerFileHandler());

                            if (Settings.TEST_MODE){
                                socketChannel.pipeline().remove("auth");
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(PORT).sync();
            future.channel().closeFuture().sync();

        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        new ServerApp().run();
    }
}
