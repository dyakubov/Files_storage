package com.geekbrains.server;

import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.FileRequest;
import com.geekbrains.common.messages.client.RenameRequest;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler extends ChannelInboundHandlerAdapter {
    private final long PART_SIZE = 1024 * 8;
    private long fileSize;
    private int parts;
    private int part;
    private Path path;
    private String serverFolder = ServerApp.getServerFolder();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest){
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get(serverFolder + fr.getFileName()))) {
                path = Paths.get(serverFolder + fr.getFileName());
                sendFile(path, ctx);
            } else {
                System.out.println("File " + fr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        } else if (msg instanceof DeleteRequest){
            DeleteRequest dr = (DeleteRequest)msg;
            if (Files.deleteIfExists(Paths.get(serverFolder + dr.getFileName()))){
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.DELETED));
            } else {
                System.out.println("File " + dr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        } else if (msg instanceof RenameRequest){
            RenameRequest rr = (RenameRequest)msg;
            if (Files.exists(Paths.get(serverFolder + rr.getFileName()))){
                path = Paths.get(serverFolder + rr.getFileName());
                if (path.toFile().renameTo(Paths.get(serverFolder + rr.getRenameTo()).toFile())){
                    ctx.writeAndFlush(new ServerMessage(ServerAnswerType.RENAMED));
                } else ctx.writeAndFlush(new ServerMessage(ServerAnswerType.ACCESS_DENIED));
            } else {
                System.out.println("File " + rr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        }

        else ctx.fireChannelRead(msg);
    }

    public void sendFile(Path path, ChannelHandlerContext ctx) throws IOException {
        FileContainer fileContainer = prepareInitFileContainer(path);
        ctx.writeAndFlush(fileContainer);
        InputStream in = new FileInputStream(path.toFile()); //FIXME
        byte[] tmp = new byte[(int) PART_SIZE];
        long offset = 0;
        long count;
        long partSize;
        while (offset != fileSize){
            count = 0;
            partSize = (int)PART_SIZE;
            if (checkIsLastPart()){
                partSize = fileSize - (part*PART_SIZE);
                tmp = new byte[(int) partSize];
                System.out.println("Last part detected: " + partSize);
            }
            while (count != partSize){
                count = in.read(tmp, 0, (int)(partSize));
                offset += count;
            }
            sendFileContainer(ctx, fileContainer, tmp);
        }
    }

    private void sendFileContainer(ChannelHandlerContext ctx, FileContainer fileContainer, byte[] tmp) {
        fileContainer.setPart(++part);
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
        if (fileSize < PART_SIZE) return 1;
        return fileSize%PART_SIZE == 0 ?
                (int)(fileSize/PART_SIZE) :
                (int)(fileSize/PART_SIZE) + 1;
    }

    private boolean checkIsLastPart(){
        return parts - part == 1;
    }
}
