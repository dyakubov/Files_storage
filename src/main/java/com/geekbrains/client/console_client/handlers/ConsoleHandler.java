package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.DeleteRequest;
import com.geekbrains.common.messages.client.FileRequest;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleHandler {
    private Scanner console;
    private Network network;

    public ConsoleHandler(Scanner console, Network network) {
        this.console = console;
        this.network = network;
    }

    public void startSession(User user) throws ClassNotFoundException, IOException {
        boolean aborted = false;
        while (!aborted) {
            System.out.println("Type command");
            String command = console.nextLine();
            switch (command.split(" ")[0]){
                case "get":
                    network.sendMsg(new FileRequest(command.split(" ")[1], user));
                    break;
                case "del":
                    network.sendMsg(new DeleteRequest(command.split(" ")[1]));
                    break;
                case "exit":
                    aborted = true;
                default:
                    System.out.println("Unknown command " + command.split(" ")[0]);
            }

           network.listenServerAnswer();
        }
    }

    public String getText(){
        return console.nextLine();
    }

    public void consoleWrite(String text){
        System.out.println(text);
    }
}
