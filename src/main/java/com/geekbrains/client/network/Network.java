package com.geekbrains.client.network;

import com.geekbrains.client.console_client.handlers.AuthHandler;
import com.geekbrains.client.console_client.handlers.ConsoleHandler;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Network {
    private static final int PORT = 8188;
    private static final String HOST = "localhost";
    private ObjectEncoderOutputStream oeos = null;
    private ObjectDecoderInputStream odis = null;
    private User user;
    private static final String userFolder = "client_storage/";

    private FileContainer fc = new FileContainer();
    private Scanner console;
    private String command;
    private ConsoleHandler ch;
    private AuthHandler ah;

    public Network(Scanner console) {
        this.console = console;
    }

    public void connect(){
        try (Socket socket = new Socket(HOST, PORT)) {
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            odis = new ObjectDecoderInputStream(socket.getInputStream());
            ch = new ConsoleHandler(console, this);
            ah = new AuthHandler(ch, this);

            ah.clientAuth();
            ch.startSession(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Object obj) {
        try {
            oeos.writeObject(obj);
            oeos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenServerAnswer() throws ClassNotFoundException, IOException {
        Object obj = odis.readObject();
        if (obj instanceof FileContainer) {
            downloadFile(obj);
        } else {
            ServerMessage sm = (ServerMessage) obj;
            System.out.println(sm.getServerAnswerType());
        }
    }

    private void downloadFile(Object obj) throws IOException, ClassNotFoundException {
        fc = (FileContainer) obj;
        System.out.printf("New file receiving: %s. Size: %d. Parts: %d %n",
                fc.getFileName(),
                fc.getSize(),
                fc.getOfParts());
        writeFileFromContainer(fc);
        while (fc.getPart() < fc.getOfParts()) {
            obj = odis.readObject();
            fc = (FileContainer) obj;
            writeFileFromContainer(fc);
        }
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0) {
            byte[] b = new byte[0];
            Files.write(Paths.get(userFolder + fc.getFileName()), b, StandardOpenOption.CREATE);
            System.out.println("Init file created. Size: " + Paths.get(userFolder + fc.getFileName()).toFile().length());
            System.out.print("Downloading...   ");
        } else {
            Files.write(Paths.get(userFolder + fc.getFileName()), fc.getData(), StandardOpenOption.APPEND);
//            System.out.printf("Written part: %d of %d. Size: %d %n",
//                    fc.getPart(), fc.getOfParts(), fc.getData().length);

            printDownloadProgress(fc.getPart(), fc.getOfParts());
        }
    }

    private void printDownloadProgress(long part, long parts){
        if (parts < 10){
            System.out.print("\b\b\b");
            System.out.print(((part/parts)*100)+"%");
            System.out.println();
            return;
        }
        if (part%(parts/10) == 0){
            System.out.print("\b\b\b");
            ///System.out.print("...");
            System.out.print(((((part*10)/parts)+1)*10)+"%");
        } else if (part == parts) {
            System.out.println();
        }
    }

    public ServerMessage readObject() throws IOException, ClassNotFoundException {
        return (ServerMessage)odis.readObject();
    }
}
