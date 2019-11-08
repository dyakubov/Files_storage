package com.geekbrains.common.messages.client;

import com.geekbrains.common.User;

public class AuthMessage extends ServiceMessage {
    private String password;
    private String login;

    public AuthMessage(User user, String password) {
        this.password = password;
        this.login = user.getLogin();
        super.setClientMessageType(ClientMessageType.AUTH);
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }
}
