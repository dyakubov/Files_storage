package com.geekbrains.server;

import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.FileRequest;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.image.Kernel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler extends ChannelInboundHandlerAdapter {
    private final long MAX_PART_SIZE = 1024 * 8;
    private InputStream in;
    private long fileSize;
    private int parts;
    private int part;
    long partSize;
    long offset;
    private Path path;
    private String serverFolder = "server_storage/";
    byte[] tmp = new byte[(int)(MAX_PART_SIZE)];

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FileRequest fr = (FileRequest) msg;
        if (Files.exists(Paths.get(serverFolder + fr.getFileName()))) {
            path = Paths.get(serverFolder + fr.getFileName());
            sendFile(path, ctx);
        } else {
            System.out.println("File " + fr.getFileName() + " not found");
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }
    }

    public void sendFile(Path path, ChannelHandlerContext ctx) throws IOException {
        FileContainer fileContainer = prepareInitFileContainer(path);
        ctx.writeAndFlush(fileContainer);
        in = new FileInputStream(path.toFile());
        tmp = new byte[(int)MAX_PART_SIZE];
        offset = 0;
        while (offset != fileSize){
            long count = 0;
            if (!checkIsLastPart()){
                partSize = (int)MAX_PART_SIZE;
            } else {
                partSize = fileSize - (part*MAX_PART_SIZE);
                tmp = new byte[(int)partSize];
                System.out.println("Last part detected: " + partSize );
            }
            while (count != partSize){
                count = in.read(tmp, 0, (int)(partSize));
                offset += count;
            }
            sendFileContainer(ctx, fileContainer, tmp);
        }
    }

    private void sendFileContainer(ChannelHandlerContext ctx, FileContainer fileContainer, byte[] tmp) {
        part++;
        fileContainer.setPart(part);
        fileContainer.setData(tmp);
        ctx.writeAndFlush(fileContainer);
        System.out.println("Sent " + part + " of " + parts + ". Size: " + fileContainer.getData().length);

    }

    @NotNull
    private FileContainer prepareInitFileContainer(Path path) {
        FileContainer fileContainer = new FileContainer(path);
        fileSize = path.toFile().length();
        parts = countParts(fileSize);
        part = 0;
        fileContainer.setOfParts(parts);
        fileContainer.setSize(fileSize);
        fileContainer.setPart(part);
        System.out.printf("Init container ready. File size: %d bytes. Parts: %d %n", fileSize, parts);
        System.out.println();
        return fileContainer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private int countParts(long fileSize){
        if (fileSize < MAX_PART_SIZE) return 1;
        return fileSize%MAX_PART_SIZE == 0 ?
                (int)(fileSize/MAX_PART_SIZE) :
                (int)(fileSize/MAX_PART_SIZE) + 1;
    }

    private boolean checkIsLastPart(){
        return parts - part == 1;
    }
}
