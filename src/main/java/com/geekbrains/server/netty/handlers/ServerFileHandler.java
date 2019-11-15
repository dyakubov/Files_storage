package com.geekbrains.server.netty.handlers;

import com.geekbrains.client.console_client.handlers.ConsoleHandler;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.server.FilesList;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import com.geekbrains.server.security.SecurityHandlers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.geekbrains.common.Settings.PART_SIZE;

public class ServerFileHandler extends ChannelInboundHandlerAdapter {
    private long fileSize;
    private int parts;
    private int part;
    private FileContainer fc;
    private ServiceMessage sm;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServiceMessage) {
            sm = (ServiceMessage) msg;
            switch (sm.getServiceMessageType()) {
                case DIR:
                    ctx.writeAndFlush(new FilesList(ServerAnswerType.FILE_LIST, getAllFiles()));
                    break;
                case GET:
                    sendFile(Paths.get(SecurityHandlers.list.get(sm.getUser().getLogin()).getUserFolder() +
                            "/" +
                            sm.getBody()),
                            ctx);
                    break;
                case DELETE:
                    deleteFile(Paths.get(SecurityHandlers.list.get(sm.getUser().getLogin()).getUserFolder() +
                            "/" +
                            sm.getBody()),
                            ctx);
                    break;
                case RENAME:
                    renameFile(Paths.get(SecurityHandlers.list.get(sm.getUser().getLogin()).getUserFolder() +
                                    "/" +
                                    sm.getBody().split(" ")[0]),
                            sm.getBody().split(" ")[1],
                            ctx);
                    break;
                default:
                    System.out.println("Another commands doesn't support");
                    break;
            }
        } else if (msg instanceof FileContainer) {
            fc = (FileContainer)msg;
            writeFileFromContainer(fc);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void renameFile(Path path, String newFileNAme, ChannelHandlerContext ctx) {
        if (Files.exists(path)) {
            if (path.toFile().renameTo(Paths.get(SecurityHandlers.list.get(sm.getUser().getLogin()).getUserFolder() + "/" + newFileNAme).toFile())) {
                ctx.writeAndFlush(new ServerMessage(ServerAnswerType.RENAMED));
            } else ctx.writeAndFlush(new ServerMessage(ServerAnswerType.ACCESS_DENIED));
        } else {
            System.out.println("File " + path.getFileName() + " not found");
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }
    }

    private void deleteFile(Path path, ChannelHandlerContext ctx) throws IOException {
        if (Files.deleteIfExists(path)) {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.DELETED));
        } else {
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }
    }

    private void sendFile(Path path, ChannelHandlerContext ctx) throws IOException {
        if (Files.exists(path)) {
            FileContainer fileContainer = prepareInitFileContainer(path);
            ctx.writeAndFlush(fileContainer);
            InputStream in = new FileInputStream(path.toFile()); //FIXME
            byte[] tmp = new byte[(int) PART_SIZE];
            long offset = 0;
            long count;
            long partSize;
            while (offset != fileSize) {
                count = 0;
                partSize = (int) PART_SIZE;
                if (checkIsLastPart()) {
                    partSize = fileSize - (part * PART_SIZE);
                    tmp = new byte[(int) partSize];
                    System.out.println("Last part detected: " + partSize);
                }
                while (count != partSize) {
                    count = in.read(tmp, 0, (int) (partSize));
                    offset += count;
                }
                sendFileContainer(ctx, fileContainer, tmp);
            }
        } else {
            System.out.println("File " + path + " not found");
            ctx.writeAndFlush(new ServerMessage(ServerAnswerType.FILE_NOT_FOUND));
        }

    }

    private void sendFileContainer(ChannelHandlerContext ctx, FileContainer fileContainer, byte[] tmp) {
        fileContainer.setPart(++part);
        fileContainer.setData(tmp);
        ctx.writeAndFlush(fileContainer);
        //System.out.println("Sent " + part + " of " + parts + ". Size: " + fileContainer.getData().length);

    }

    @NotNull
    private FileContainer prepareInitFileContainer(Path path) {
        FileContainer fileContainer = new FileContainer(path, null);
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

    private int countParts(long fileSize) {
        if (fileSize < PART_SIZE) return 1;
        return fileSize % PART_SIZE == 0 ?
                (int) (fileSize / PART_SIZE) :
                (int) (fileSize / PART_SIZE) + 1;
    }

    private boolean checkIsLastPart() {
        return parts - part == 1;
    }

    private List<String> getAllFiles() {
        try {
            return Files.walk(SecurityHandlers.list.get(sm.getUser().getLogin()).getUserFolder()).filter(Files::isRegularFile).filter(f -> {
                try {
                    return !Files.isHidden(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }).map(Path::toFile).map(File::getName).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0) {
            byte[] b = new byte[0];
            Files.write(Paths.get(SecurityHandlers.list.get(fc.getUser().getLogin()).getUserFolder() + "/" + fc.getFileName()), b, StandardOpenOption.CREATE);
            System.out.println("Init file created");
            System.out.print("Downloading...   ");
        } else {
            Files.write(Paths.get(SecurityHandlers.list.get(fc.getUser().getLogin()).getUserFolder() + "/" + fc.getFileName()), fc.getData(), StandardOpenOption.APPEND);
            ConsoleHandler.printProgressBar(fc.getPart(), fc.getOfParts());
        }
    }
}