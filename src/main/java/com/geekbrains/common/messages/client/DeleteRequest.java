package com.geekbrains.common.messages.client;

public class DeleteRequest extends ServiceMessage {
    private String fileName;

    public DeleteRequest(String fileName) {
        this.fileName = fileName;
        super.setClientMessageType(ClientMessageType.DELETE);
    }

    public String getFileName() {
        return fileName;
    }
}
