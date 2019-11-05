package com.geekbrains.client;

import java.io.*;
import java.net.Socket;

public class App implements Runnable {
    private final int PORT = 8189;
    private final String HOST = "localhost";
    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, PORT);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())){
            System.out.println("Client started");
            out.write(15);
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
