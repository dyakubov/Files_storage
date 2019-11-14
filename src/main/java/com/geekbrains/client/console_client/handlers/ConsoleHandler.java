package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.User;

import java.io.IOException;
import java.util.Scanner;
public class ConsoleHandler {
    private Scanner console;
    private Network network;
    private String command;

    public ConsoleHandler(Scanner console, Network network) {
        this.console = console;
        this.network = network;
    }

    void startSession(User user) throws ClassNotFoundException, IOException {
        ClientFileHandler cfh = network.getCfh();
        while (true) {
            write("Type command");
            command = "";
            command = console.nextLine();
            switch (parseCommand(command)[0]){
                case "get":
                    cfh.sendFileRequest(parseCommand(command)[1]);
                    break;
                case "del":
                    cfh.sendDeleteRequest(parseCommand(command)[1]);
                    break;
                case "rename":
                    if (parseCommand(command).length < 3){
                        write("Wrong request format. Example: rename 1.txt 2.txt");
                        break;
                    }
                    cfh.sendRenameRequest(parseCommand(command)[1], parseCommand(command)[2]);
                    break;
                case "upload":
                    cfh.sendFile(parseCommand(command)[1]);
                    break;
                case "dir":
                    cfh.allFilesRequest();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown command " + command);
                    break;
            }
        }
    }

    public String getText(){
        return console.nextLine();
    }

    public void write(String text){
        System.out.println(text);
    }

    public static void printProgressBar(long part, long parts){
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
    private String[] parseCommand(String command){
        return command.split(" ");
    }
}
