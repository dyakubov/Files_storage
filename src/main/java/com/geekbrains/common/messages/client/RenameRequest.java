package com.geekbrains.common.messages.client;

public class RenameRequest extends ServiceMessage {
    private String fileName;
    private String renameTo;

    public RenameRequest(String fileName, String renameTo) {
        this.fileName = fileName;
        this.renameTo = renameTo;
        super.setClientMessageType(ClientMessageType.RENAME);
    }

    public String getFileName() {
        return fileName;
    }

    public String getRenameTo() {
        return renameTo;
    }
}
