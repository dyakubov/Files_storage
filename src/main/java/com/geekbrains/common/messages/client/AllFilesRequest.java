package com.geekbrains.common.messages.client;

public class AllFilesRequest extends ServiceMessage {
    public AllFilesRequest() {
        super.setClientMessageType(ClientMessageType.GET_ALL);
    }
}
