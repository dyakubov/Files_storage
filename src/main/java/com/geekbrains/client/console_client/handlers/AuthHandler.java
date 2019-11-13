package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.AuthMessage;
import com.geekbrains.common.messages.client.RegistrationRequest;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;

import java.io.IOException;

public class AuthHandler {
    private boolean authOK = true; //User always authorized //TODO change to false after created auth algorithm
    private ConsoleHandler ch;
    private Network network;

    public AuthHandler(ConsoleHandler ch, Network network) {
        this.ch = ch;
        this.network = network;
    }

    public void clientAuth() throws ClassNotFoundException, IOException {
        while (!authOK) {
            ch.write("Please auth");
            ch.write("Type login");
            User user = new User(-1, ch.getText());
            System.out.println("Type pass");
            String pass = ch.getText();
            network.sendMsg(new AuthMessage(user, pass));
            ServerMessage sm = (ServerMessage) network.readObject();
            if (sm.getServerAnswerType().equals(ServerAnswerType.AUTH_OK)) {
                ch.write("Auth OK");
                //ch.startSession(user); //TODO uncomment after created auth algorithm
                authOK = true;

            } else if (sm.getServerAnswerType().equals(ServerAnswerType.USER_NOT_FOUND)) {
                boolean regOK = false;
                while (!regOK) {
                    ch.write("Please register");
                    ch.write("Type login");
                    String login = ch.getText();
                    ch.write("Type pass");
                    pass = ch.getText();
                    network.sendMsg(new RegistrationRequest(login, pass));
                    sm = (ServerMessage) network.readObject();
                    if (sm.getServerAnswerType().equals(ServerAnswerType.REG_OK)) {
                        regOK = true;
                        ch.write("Registration complete");
                    } else ch.write(sm.getServerAnswerType().toString());
                }
            }
        }

        //TODO delete after created auth algorithm
        ch.startSession(new User(1, "test"));
    }


}
