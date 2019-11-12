package com.geekbrains.client;
import com.geekbrains.client.network.Network;

import java.util.Scanner;

public class ClientApp implements Runnable {


    public static void main(String[] args) {
        new ClientApp().run();
    }

    @Override
    public void run() {
        Scanner console = new Scanner(System.in);
        Network network = new Network(console);
        network.connect();
    }














}

