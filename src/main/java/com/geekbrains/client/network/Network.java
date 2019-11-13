package com.geekbrains.client.network;

import com.geekbrains.client.console_client.handlers.AuthHandler;
import com.geekbrains.client.console_client.handlers.ClientFileHandler;
import com.geekbrains.client.console_client.handlers.ConsoleHandler;
import com.geekbrains.common.FileContainer;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.server.FilesList;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Network {
    private static final int PORT = 8188;
    private static final String HOST = "localhost";
    private ObjectEncoderOutputStream oeos = null;
    private ObjectDecoderInputStream odis = null;
    private User user;
    public static final String userFolder = "client_storage/";

    private FileContainer fc = new FileContainer();
    private Scanner console;
    private String command;
    private AuthHandler ah;
    private ClientFileHandler cfh;
    private ConsoleHandler ch;

    public Network(Scanner console) {
        this.console = console;
    }

    public void connect(){
        try (Socket socket = new Socket(HOST, PORT)) {
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            odis = new ObjectDecoderInputStream(socket.getInputStream());

            initHandlers();

            ah.clientAuth();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initHandlers() {
        ch = new ConsoleHandler(console, this);
        ah = new AuthHandler(ch, this);
        cfh = new ClientFileHandler(this);
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
            fc = (FileContainer)obj;
            cfh.downloadFile(fc);
        } else if (obj instanceof FilesList) {
            FilesList fl = (FilesList)obj;
            cfh.printAllFiles(fl.getAllFiles());
        } else {
            ServerMessage sm = (ServerMessage) obj;
            System.out.println(sm.getServerAnswerType());
        }
    }




    public Object readObject() throws IOException, ClassNotFoundException {
        return odis.readObject();
    }

    public ConsoleHandler getConsole() {
        return ch;
    }

    public User getUser() {
        return user;
    }

    public ClientFileHandler getCfh() {
        return cfh;
    }
}