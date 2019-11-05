package com.geekbrains.common;

public class AuthMessage implements AbstractMessage {
    private byte type;
    private byte[] authData;

    public AuthMessage(String login, String password) {
        this.type = Commands.AUTH;
        this.authData = new byte[]{Byte.parseByte(login), Byte.parseByte(password)};
    }
    @Override
    public byte getType(){
        return this.type;
    }

    @Override
    public byte[] getAuthData(){
        return authData;
    }

}
