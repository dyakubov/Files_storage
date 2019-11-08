package com.geekbrains.common.messages.client;

public class FileRequest extends ServiceMessage {
    private String fileName;

    public FileRequest(String fileName) {
        this.fileName = fileName;
        super.setClientMessageType(ClientMessageType.GET);
    }

    public String getFileName() {
        return fileName;
    }
}
