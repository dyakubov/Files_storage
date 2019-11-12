package com.geekbrains.client;

import com.geekbrains.common.FileContainer;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.AuthMessage;
import com.geekbrains.common.messages.client.FileRequest;
import com.geekbrains.common.messages.client.RegistrationRequest;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class ClientApp implements Runnable {
    private final int PORT = 8188;
    private final String HOST = "localhost";
    private final long MAX_PART_SIZE = 1024 * 8;
    ObjectEncoderOutputStream oeos = null;
    ObjectDecoderInputStream odis = null;
    ServerMessage sm;
    Scanner console;
    User user;
    String userFolder = "client_storage/";
    private boolean authOK = true;
    private boolean regOK = true;

    public static void main(String[] args) {
        ClientApp app = new ClientApp();
        app.run();
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, PORT)) {

            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            odis = new ObjectDecoderInputStream(socket.getInputStream());
            console = new Scanner(System.in);

            while (socket.isConnected()) {
                if (!authOK) {
                    System.out.println("Please auth");
                    System.out.println("Type login");
                    user = new User(-1, console.nextLine());
                    System.out.println("Type pass");
                    String pass = console.nextLine();
                    sendMsg(new AuthMessage(user, pass));
                    sm = (ServerMessage) odis.readObject();
                    if (sm.getServerAnswerType().equals(ServerAnswerType.AUTH_OK)) {
                        System.out.println("Auth OK");
                        authOK = true;
                    } else if (sm.getServerAnswerType().equals(ServerAnswerType.USER_NOT_FOUND)) {
                        regOK = false;
                        while (!regOK) {
                            System.out.println("Please register");
                            String login = console.nextLine();
                            System.out.println("Type pass");
                            pass = console.nextLine();
                            sendMsg(new RegistrationRequest(login, pass));
                            sm = (ServerMessage) odis.readObject();
                            if (sm.getServerAnswerType().equals(ServerAnswerType.REG_OK)) {
                                regOK = true;
                                System.out.println("Registration complete");
                            }
                        }
                    }
                } else {
                    System.out.println("Type command");
                    String command = console.nextLine();
                    if (command.startsWith("get")) {
                        sendMsg(new FileRequest(command.split(" ")[1], user));
                    }
                    Object obj = odis.readObject();
                    if (obj instanceof FileContainer) {
                        FileContainer fc = (FileContainer) obj;
                        System.out.printf("New file receiving: %s. Size: %d. Parts: %d %n",
                                fc.getFileName(),
                                fc.getSize(),
                                fc.getOfParts());
                        while (fc.getPart() < fc.getOfParts()){
                            obj = odis.readObject();
                            fc = (FileContainer) obj;
                            writeFileFromContainer(fc);
                        }

                    } else {
                        ServerMessage sm = (ServerMessage) obj;
                        System.out.println(sm.getServerAnswerType());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(Object obj) {
        try {
            oeos.writeObject(obj);
            oeos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFileFromContainer(FileContainer fc) throws IOException {
        if (fc.getPart() == 0){
            byte[] b = new byte[0];
            Files.write(Paths.get(userFolder+fc.getFileName()), b, StandardOpenOption.CREATE);
            System.out.println("Init file created. Size: " + Paths.get(userFolder+fc.getFileName()).toFile().length());
        } else {
            Files.write(Paths.get(userFolder+fc.getFileName()) , fc.getData(), StandardOpenOption.APPEND);
            System.out.printf("Written part: %d of %d. Size: %d %n",
                    fc.getPart(), fc.getOfParts(), fc.getData().length);
        }
    }
}

