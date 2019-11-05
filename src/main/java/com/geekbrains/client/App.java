package com.geekbrains.client;

import java.io.*;
import java.net.Socket;

public class App implements Runnable {
    private final int PORT = 4004;
    private final String HOST = "localhost";
    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            if (socket.isConnected()) System.out.println("Client started");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop() throws IOException {
        this.socket.close();
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}
