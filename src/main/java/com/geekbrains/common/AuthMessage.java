package com.geekbrains.common;

public class AuthMessage implements AbstractMessage {
    private String type;
    private String[] authData;

    public AuthMessage(String login, String password) {
        this.type = Commands.MessageType.AUTH;
        this.authData = new String[2];
        authData[0] = login;
        authData[1] = password;
    }
    @Override
    public String getType(){
        return this.type;
    }

    @Override
    public String[] getData(){
        return authData;
    }

    public String getLogin(){
        return authData[0];
    }
    public String getPassword(){
        return authData[1];
    }

}
