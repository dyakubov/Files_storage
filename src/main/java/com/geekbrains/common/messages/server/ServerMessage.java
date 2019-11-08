package com.geekbrains.common.messages.server;

import com.geekbrains.common.FileContainer;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private ServerAnswerType serverAnswerType;
    private FileContainer fileContainer;

    public ServerMessage(ServerAnswerType serverAnswerType) {
        this.serverAnswerType = serverAnswerType;
    }

    public ServerMessage(ServerAnswerType serverAnswerType, FileContainer fileContainer) {
        this.serverAnswerType = serverAnswerType;
        this.fileContainer = fileContainer;
    }

    public ServerAnswerType getServerAnswerType() {
        return serverAnswerType;
    }

    public FileContainer getFileContainer() {
        return fileContainer;
    }
}
