package com.geekbrains.common.messages.client;

import com.geekbrains.common.User;

public class FileRequest extends ServiceMessage {
    private String fileName;

    public FileRequest(String fileName, User user) {
        this.fileName = fileName;
        super.setClientMessageType(ClientMessageType.GET);
        super.setUser(user);
    }

    public String getFileName() {
        return fileName;
    }
}
