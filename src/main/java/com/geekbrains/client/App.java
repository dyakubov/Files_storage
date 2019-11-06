package com.geekbrains.client;

import com.geekbrains.server.server.ProtocolHandler;

import java.io.*;
import java.net.Socket;
import com.geekbrains.common.Commands;

public class App implements Runnable {
    private final int PORT = 8189;
    private final String HOST = "localhost";
    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, PORT);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())){
            System.out.println("Client started");
            ProtocolHandler ph = new ProtocolHandler();
            String message = ph.compileMessage(Commands.MessageType.AUTH, "ivan 123");
            out.write(message.getBytes());
            out.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            message = ph.compileMessage(Commands.MessageType.DELETE_FILE, "1.txt");
            out.write(message.getBytes());
            out.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            message = ph.compileMessage(Commands.MessageType.GET_FILE, "3.txt");
            out.write(message.getBytes());
            out.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            message = ph.compileMessage(Commands.MessageType.RENAME_FILE, "3.txt 5.txt");
            out.write(message.getBytes());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}
