package com.geekbrains.common.messages.client;

public abstract class ServiceMessage extends AbstractMessage {
    private ClientMessageType clientMessageType;

    public ClientMessageType getClientMessageType() {
        return clientMessageType;
    }

    public void setClientMessageType(ClientMessageType clientMessageType) {
        this.clientMessageType = clientMessageType;
    }
}
