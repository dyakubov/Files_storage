package com.geekbrains.common.messages.client;

public class UploadRequest extends ServiceMessage {
    private String filename;

    public UploadRequest(String filename) {
        this.filename = filename;
        super.setClientMessageType(ClientMessageType.UPLOAD);
    }

    public String getFilename() {
        return filename;
    }
}
