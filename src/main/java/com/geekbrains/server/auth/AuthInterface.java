package com.geekbrains.server.auth;

import com.geekbrains.common.User;
import com.geekbrains.common.messages.server.ServerMessage;

import java.sql.SQLException;

public interface AuthInterface {
    ServerMessage checkAuth(String login, String pass);
    ServerMessage regUser(String login, String pass) throws SQLException;
    boolean userExist(String login) throws SQLException;
}
