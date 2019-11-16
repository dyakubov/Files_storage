package com.geekbrains.common.messages.server;

import com.geekbrains.common.FileContainer;
import com.geekbrains.common.User;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private ServerAnswerType serverAnswerType;
    private String message;

    public ServerMessage(ServerAnswerType serverAnswerType) {
        this.serverAnswerType = serverAnswerType;
    }
    public ServerMessage(ServerAnswerType serverAnswerType, String message) {
        this.serverAnswerType = serverAnswerType;
        this.message = message;
    }

    public ServerAnswerType getServerAnswerType() {
        return serverAnswerType;
    }
    public String getMessage() {
        return message;
    }
}
