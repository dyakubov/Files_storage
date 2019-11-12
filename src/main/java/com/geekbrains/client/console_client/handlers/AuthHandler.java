package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.AuthMessage;
import com.geekbrains.common.messages.client.RegistrationRequest;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;

import java.io.IOException;

public class AuthHandler {
    private boolean authOK = true;
    private boolean regOK = true;
    private ConsoleHandler ch;
    private Network network;
    private ServerMessage sm;

    public AuthHandler(ConsoleHandler ch, Network network) {
        this.ch = ch;
        this.network = network;
    }

    public void clientAuth() throws ClassNotFoundException, IOException {
        while (!authOK) {
            System.out.println("Please auth");
            System.out.println("Type login");
            User user = new User(-1, ch.getText());
            System.out.println("Type pass");
            String pass = ch.getText();
            network.sendMsg(new AuthMessage(user, pass));
            sm = network.readObject();
            if (sm.getServerAnswerType().equals(ServerAnswerType.AUTH_OK)) {
                System.out.println("Auth OK");
                authOK = true;
            } else if (sm.getServerAnswerType().equals(ServerAnswerType.USER_NOT_FOUND)) {
                regOK = false;
                while (!regOK) {
                    System.out.println("Please register");
                    System.out.println("Type login");
                    String login = ch.getText();
                    System.out.println("Type pass");
                    pass = ch.getText();
                    network.sendMsg(new RegistrationRequest(login, pass));
                    sm = network.readObject();
                    if (sm.getServerAnswerType().equals(ServerAnswerType.REG_OK)) {
                        regOK = true;
                        System.out.println("Registration complete");
                    }
                }
            }
        }
    }
}
