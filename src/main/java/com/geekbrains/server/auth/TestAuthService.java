package com.geekbrains.server.auth;

import com.geekbrains.common.User;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import com.geekbrains.server.security.SecurityHandler;
import com.geekbrains.server.users.Users;

import java.io.IOException;

public class TestAuthService implements AuthInterface {
    private Users users;
    private SecurityHandler securityHandler;

    public TestAuthService(Users users) {
        this.users = users;
    }

    @Override
    public ServerMessage checkAuth(String login, String pass) {
        if (userExist(login)) {
            if (users.getAllUsers().get(login).getPass().equals(pass)) {
                System.out.println("Login OK for user: " + login);
                securityHandler = new SecurityHandler(users.getAllUsers().get(login));
                securityHandler.createUserFolder(users.getAllUsers().get(login));
                return new ServerMessage(ServerAnswerType.AUTH_OK);
            } else {
                System.out.println("Wrong password for user:" + login);
                return new ServerMessage(ServerAnswerType.AUTH_FAILED);
            }
        } else {
            System.out.println("User " + login + " not found");
            return new ServerMessage(ServerAnswerType.USER_NOT_FOUND);
        }
    }

    @Override
    public ServerMessage regUser(String login, String pass)  {
        if (userExist(login)){
            return new ServerMessage(ServerAnswerType.USER_ALREADY_EXIST);
        } else {
            User user = new User(-1, login,pass);
            users.addToAllUserAndReturnID(user);
            return new ServerMessage(ServerAnswerType.REG_OK);

        }
    }

    @Override
    public boolean userExist(String login) {
        return users.getAllUsers().containsKey(login);
    }
}
