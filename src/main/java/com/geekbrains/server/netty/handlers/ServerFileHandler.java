package com.geekbrains.server.netty.handlers;

import com.geekbrains.client.console_client.handlers.ConsoleHandler;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.AllFilesRequest;
import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.FileRequest;
import com.geekbrains.common.messages.client.RenameRequest;
import com.geekbrains.common.messages.server.FilesList;
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
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.geekbrains.common.Settings.*;

public class ServerFileHandler extends ChannelInboundHandlerAdapter {
    private long fileSize;
    private int parts;
    private int part;
    private Path path;
    private FileContainer fc;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest){
            FileRequest fr = (FileRequest) msg;
            System.out.printf("%s: %s%n", fr.getClass(), fr.getFileName());
            if (Files.exists(Paths.get(SERVER_FOLDER + fr.getFileName()))) {
                path = Paths.get(SERVER_FOLDER + fr.getFileName());
                sendFile(path, ctx);
            } else {
                System.out.println("File " + fr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        } else if (msg instanceof DeleteRequest){
            DeleteRequest dr = (DeleteRequest)msg;
            System.out.printf("%s: %s%n", dr.getClass(), dr.getFileName());
            if (Files.deleteIfExists(Paths.get(SERVER_FOLDER + dr.getFileName()))){
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.DELETED));
            } else {
                System.out.println("File " + dr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        } else if (msg instanceof RenameRequest){
            RenameRequest rr = (RenameRequest)msg;
            System.out.printf("%s: %s%n", rr.getClass(), rr.getFileName());
            if (Files.exists(Paths.get(SERVER_FOLDER + rr.getFileName()))){
                path = Paths.get(SERVER_FOLDER + rr.getFileName());
                if (path.toFile().renameTo(Paths.get(SERVER_FOLDER + rr.getRenameTo()).toFile())){
                    ctx.writeAndFlush(new ServerMessage(ServerAnswerType.RENAMED));
                } else ctx.writeAndFlush(new ServerMessage(ServerAnswerType.ACCESS_DENIED));
            } else {
                System.out.println("File " + rr.getFileName() + " not found");
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
            }
        } else if (msg instanceof FileContainer) {
            fc = (FileContainer)msg;
            writeFileFromContainer(fc);
        } if (msg instanceof AllFilesRequest){
            System.out.println("AllFilesRequest");
            ctx.writeAndFlush(new FilesList(ServerAnswerType.FILE_LIST, getAllFiles()));
        }

        else ctx.fireChannelRead(msg);
    }

    private void sendFile(Path path, ChannelHandlerContext ctx) throws IOException {
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

    private List<String> getAllFiles() throws IOException {
        return Files.walk(Paths.get(SERVER_FOLDER)).filter(Files::isRegularFile).filter(f -> {
            try {
                return !Files.isHidden(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }).map(Path::toFile).map(File::getName).collect(Collectors.toList());
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0) {
            byte[] b = new byte[0];
            Files.write(Paths.get(SERVER_FOLDER + fc.getFileName()), b, StandardOpenOption.CREATE);
            System.out.println("Init file created");
            System.out.print("Downloading...   ");
        } else {
            Files.write(Paths.get(SERVER_FOLDER + fc.getFileName()), fc.getData(), StandardOpenOption.APPEND);
//            System.out.printf("Written part: %d of %d. Size: %d %n",
//                    fc.getPart(), fc.getOfParts(), fc.getData().length);
            ConsoleHandler.printProgressBar(fc.getPart(), fc.getOfParts());
        }
    }
}