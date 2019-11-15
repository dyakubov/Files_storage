package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


import static com.geekbrains.common.Settings.PART_SIZE;
import static com.geekbrains.common.Settings.USER_FOLDER;

public class ClientFileHandler {
    private Network network;
    private ConsoleHandler ch;

    private long fileSize;
    private int parts;
    private int part;

    public ClientFileHandler(Network network) {
        this.network = network;
        this.ch = network.getConsole();
    }

    public void downloadFile(FileContainer fc) throws IOException, ClassNotFoundException {
        writeFileFromContainer(fc);
        while (fc.getPart() < fc.getOfParts()) {
            fc = (FileContainer) network.readObject();
            writeFileFromContainer(fc);
        }
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0) {
            byte[] b = new byte[0];
            Files.write(Paths.get(USER_FOLDER + fc.getFileName()), b, StandardOpenOption.CREATE);
            ch.writeLine("Init file created");
            System.out.print("Downloading...   ");
        } else {
            Files.write(Paths.get(USER_FOLDER + fc.getFileName()), fc.getData(), StandardOpenOption.APPEND);
//            System.out.printf("Written part: %d of %d. Size: %d %n",
//                    fc.getPart(), fc.getOfParts(), fc.getData().length);
            ConsoleHandler.printProgressBar(fc.getPart(), fc.getOfParts());
        }
    }

    static boolean fileExistOnClient(String fileName){
        return Files.exists(Paths.get(USER_FOLDER + fileName));
    }

    public void printAllFiles(List<String> list){
        {
            if (!list.isEmpty()){
                list.forEach(System.out::println);
            } else ch.writeLine("No such files in the folder");

        }
    }

    void sendFile(String filename) throws IOException {
        FileContainer fileContainer = prepareInitFileContainer(Paths.get(USER_FOLDER + filename));
        network.sendMsg(fileContainer);
        InputStream in = new FileInputStream(Paths.get(USER_FOLDER + filename).toFile()); //FIXME
        byte[] tmp = new byte[(int) PART_SIZE];
        long offset = 0;
        long count;
        long partSize;
        System.out.print("Uploading...   ");
        while (offset != fileSize){
            count = 0;
            partSize = (int)PART_SIZE;
            if (checkIsLastPart()){
                partSize = fileSize - (part*PART_SIZE);
                tmp = new byte[(int) partSize];
                //System.out.println("Last part detected: " + partSize);
            }
            while (count != partSize){
                count = in.read(tmp, 0, (int)(partSize));
                offset += count;
            }
            fileContainer.setPart(++part);
            fileContainer.setData(tmp);
            network.sendMsg(fileContainer);
            //System.out.println("Sent " + part + " of " + parts + ". Size: " + fileContainer.getData().length);
            ConsoleHandler.printProgressBar(part, parts);
        }
    }

    @NotNull
    private FileContainer prepareInitFileContainer(Path path) {
        FileContainer fileContainer = new FileContainer(path, network.getUser());
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
