package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.messages.client.AllFilesRequest;
import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.FileRequest;
import com.geekbrains.common.messages.client.RenameRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.geekbrains.client.network.Network.userFolder;

public class ClientFileHandler {
    private Network network;
    private ConsoleHandler ch;

    public ClientFileHandler(Network network) {
        this.network = network;
        this.ch = network.getConsole();
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0) {
            byte[] b = new byte[0];
            Files.write(Paths.get(userFolder + fc.getFileName()), b, StandardOpenOption.CREATE);
            ch.write("Init file created");
            System.out.print("Downloading...   ");
        } else {
            Files.write(Paths.get(userFolder + fc.getFileName()), fc.getData(), StandardOpenOption.APPEND);
//            System.out.printf("Written part: %d of %d. Size: %d %n",
//                    fc.getPart(), fc.getOfParts(), fc.getData().length);
            ConsoleHandler.printDownloadProgress(fc.getPart(), fc.getOfParts());
        }
    }

    public void downloadFile(FileContainer fc) throws IOException, ClassNotFoundException {
        System.out.printf("New file: %s. Size: %d. Parts: %d %n",
        fc.getFileName(),
                fc.getSize(),
                fc.getOfParts());
        writeFileFromContainer(fc);
        while (fc.getPart() < fc.getOfParts()) {
            fc = (FileContainer) network.readObject();
            writeFileFromContainer(fc);
        }
    }
    private static boolean fileExistOnClient(String fileName){
        return Files.exists(Paths.get(userFolder + fileName));
    }

    void sendFileRequest(String fileName) throws IOException, ClassNotFoundException {
        if (fileExistOnClient(fileName)){
            ch.write("File " + fileName + " already exist");
            ch.write("Overwrite? Y/N");
            if (ch.getText().equals("Y")){
                if (Files.deleteIfExists(Paths.get(userFolder + fileName))){
                    network.sendMsg(new FileRequest(fileName, network.getUser()));
                    network.listenServerAnswer();
                }
            } else {
                ch.write("Aborted");
            }
        } else {
            network.sendMsg(new FileRequest(fileName, network.getUser()));
            network.listenServerAnswer();
        }
    }

    void sendDeleteRequest(String filename) throws IOException, ClassNotFoundException {
        ch.write("Do you really want to delete file " + filename + " from server? Y/N");
        if (ch.getText().equals("Y")){
            network.sendMsg(new DeleteRequest(filename));
            network.listenServerAnswer();
        } else {
            ch.write("Aborted");
        }
    }

    void sendRenameRequest(String filename, String newFileName) throws IOException, ClassNotFoundException {
        network.sendMsg(new RenameRequest(filename, newFileName));
        network.listenServerAnswer();
    }

    void allFilesRequest() throws IOException, ClassNotFoundException {
        network.sendMsg(new AllFilesRequest());
        network.listenServerAnswer();
    }

    public void printAllFiles(List<String> list){
        list.forEach(System.out::println);
    }
}
