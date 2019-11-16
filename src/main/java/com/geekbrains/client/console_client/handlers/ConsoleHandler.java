package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.client.ServiceMessageType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static com.geekbrains.common.Settings.USER_FOLDER;

public class ConsoleHandler {
    private Scanner console;
    private Network network;

    public ConsoleHandler(Scanner console, Network network) {
        this.console = console;
        this.network = network;
    }

    void startSession(User user) throws ClassNotFoundException, IOException {
        ClientFileHandler cfh = network.getCfh();
        while (true) {
            writeLine("Type command");
            String command = console.nextLine().toLowerCase();
            switch (parseCommand(command)[0]){
                case "get":
                    sendFileRequest(user, command);
                    break;
                case "del":
                    sendDelRequest(user, parseCommand(command)[1]);
                    break;
                case "rename":
                    sendRenameRequest(user, command);
                    break;
                case "upload":
                    cfh.sendFile(parseCommand(command)[1]);
                    break;
                case "dir":
                    sendDirRequest(user);
                    break;
                case "exit":
                    System.exit(0);
                    break;

                case "help":
                    printHelpFile();
                    break;
                default:
                    writeLine("Unknown command " + command);
                    break;
            }
        }
    }

    private void printHelpFile() throws IOException {
        BufferedReader fin = new BufferedReader(new FileReader("src/main/resources/help.txt"));
        String line;
        while ((line = fin.readLine()) != null) {
            writeLine(line);
        }
    }

    private void sendDirRequest(User user) throws ClassNotFoundException, IOException {
        network.sendMsg(new ServiceMessage(ServiceMessageType.DIR, user));
        network.listenServerAnswer();
    }

    private void sendRenameRequest(User user, String command) throws ClassNotFoundException, IOException {
        if (parseCommand(command).length < 3){
            writeLine("Wrong request format. Type 'help' to get instructions");
            return;
        }
        network.sendMsg(new ServiceMessage(ServiceMessageType.RENAME, user, parseCommand(command)[1] + " " + parseCommand(command)[2]));
        network.listenServerAnswer();
    }

    private void sendFileRequest(User user, String command) throws IOException, ClassNotFoundException {
        if (parseCommand(command).length < 2){
            writeLine("Unknown command " + command);
            return;
        }
        String fileName = parseCommand(command)[1];
        if (ClientFileHandler.fileExistOnClient(parseCommand(command)[1])){
            writeLine("File " + fileName + " already exist");
            writeLine("Overwrite? Y/N");
            if (getText().equals("Y")){
                if (Files.deleteIfExists(Paths.get(USER_FOLDER + fileName))){
                    network.sendMsg(new ServiceMessage(ServiceMessageType.GET, user, fileName));
                    network.listenServerAnswer();
                }
            } else {
                writeLine("Aborted");
            }
        } else {
            network.sendMsg(new ServiceMessage(ServiceMessageType.GET, user, fileName));
            network.listenServerAnswer();
        }
    }

    private void sendDelRequest(User user, String filename) throws ClassNotFoundException, IOException {
        writeLine("Do you really want to delete file " + filename + " from server? Y/N");
        if (getText().equals("Y")){
            network.sendMsg(new ServiceMessage(ServiceMessageType.DELETE, user, filename));
            network.listenServerAnswer();
        } else {
            writeLine("Aborted");
        }
    }

    String getText(){
        return console.nextLine();
    }

    public void writeLine(String text){
        System.out.println(text);
    }

    public void write(String text){
        System.out.print(text);
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
            System.out.print(((((part*10)/parts)+1)*10)+"%");
        } else if (part == parts) {
            System.out.println();
        }
    }
    private String[] parseCommand(String command){
        return command.split(" ");
    }
}
