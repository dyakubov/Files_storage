package com.geekbrains.client;

import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.AuthMessage;
import com.geekbrains.common.messages.server.ServerMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;
import java.net.Socket;

public class ClientApp implements Runnable {
    private final int PORT = 8188;
    private final String HOST = "localhost";
    ObjectEncoderOutputStream oeos = null;
    ObjectDecoderInputStream odis = null;
    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, PORT)) {
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            User user = new User("ivan");
        AuthMessage authMessage = new AuthMessage(user, "234");
        oeos.writeObject(authMessage);
        oeos.flush();
        odis = new ObjectDecoderInputStream(socket.getInputStream());
        ServerMessage msgFromServer = (ServerMessage) odis.readObject();
        System.out.println("Answer from server: " + msgFromServer.getServerAnswerType());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ClientApp app = new ClientApp();
        app.run();
    }
}
