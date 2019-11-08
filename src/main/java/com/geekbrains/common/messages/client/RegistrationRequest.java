package com.geekbrains.common.messages.client;

public class RegistrationRequest extends ServiceMessage {
    private String login;
    private String password;

    public RegistrationRequest(String login, String password) {
        this.login = login;
        this.password = password;
        super.setClientMessageType(ClientMessageType.REG);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
