package com.geekbrains.client.console_client.handlers;

import com.geekbrains.client.network.Network;
import com.geekbrains.common.Settings;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.client.ServiceMessage;
import com.geekbrains.common.messages.client.ServiceMessageType;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import com.geekbrains.server.netty.ServerApp;

import java.io.IOException;

public class AuthHandler {
    private boolean authOK = Settings.TEST_MODE;
    private ConsoleHandler ch;
    private Network network;

    public AuthHandler(ConsoleHandler ch, Network network) {
        this.ch = ch;
        this.network = network;
    }

    public void clientAuth() throws ClassNotFoundException, IOException {
        while (!authOK) {
            ch.writeLine("Please auth");
            ch.writeLine("Type login");
            String login = ch.getText();
            System.out.println("Type pass");
            String pass = ch.getText();
            User user = new User(-1, login, pass);
            network.sendMsg(new ServiceMessage(ServiceMessageType.AUTH, user));
            ServerMessage sm = (ServerMessage) network.readObject();
            if (sm.getServerAnswerType().equals(ServerAnswerType.AUTH_OK)) {
                ch.writeLine("Auth OK");
                network.setUser(user);
                ch.startSession(user); //TODO uncomment after created auth algorithm
                authOK = true;
                return;

            } else if (sm.getServerAnswerType().equals(ServerAnswerType.USER_NOT_FOUND)) {
                boolean regOK = false;

                while (!regOK) {
                    ch.writeLine("Please register");
                    ch.writeLine("Type login");
                    login = ch.getText();
                    ch.writeLine("Type pass");
                    pass = ch.getText();
                    user = new User(-1, login, pass);
                    network.sendMsg(new ServiceMessage(ServiceMessageType.REG, user));
                    sm = (ServerMessage) network.readObject();
                    if (sm.getServerAnswerType().equals(ServerAnswerType.REG_OK)) {
                        regOK = true;
                        ch.writeLine("Registration complete");
                    } else ch.writeLine(sm.getServerAnswerType().toString());
                }
            }
        }

        //TODO delete after created auth algorithm
        ch.startSession(new User(1, "test", "test"));
    }


}
