package com.geekbrains.server.auth.inner_auth;

import com.geekbrains.common.Settings;
import com.geekbrains.common.User;
import com.geekbrains.common.messages.server.ServerAnswerType;
import com.geekbrains.common.messages.server.ServerMessage;
import com.geekbrains.server.auth.AuthInterface;
import com.geekbrains.server.security.UsersHandler;
import com.geekbrains.server.security.Users;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestAuthService implements AuthInterface {
    private com.geekbrains.server.auth.inner_auth.Users users;


    public TestAuthService(com.geekbrains.server.auth.inner_auth.Users users) {
        this.users = users;
        if (Files.exists(Paths.get(Settings.SAVED_USERS_FOLDER))){
            restoreUsers();
        }
    }

    @Override
    public ServerMessage checkAuth(String login, String pass) {
        if (userExist(login)) {
            if (users.getAllUsers().get(login).getPass().equals(pass)) {
                System.out.println("Login OK for user: " + login);
                Users.list.put(login, new UsersHandler(users.getAllUsers().get(login)));
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
            users.addToAllUsers(user);
            saveUsers();
            return new ServerMessage(ServerAnswerType.REG_OK);

        }
    }

    @Override
    public boolean userExist(String login) {
        return users.getAllUsers().containsKey(login);
    }

    private void restoreUsers() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(Settings.SAVED_USERS_FOLDER);
        } catch (FileNotFoundException e) {
            System.out.println("No saved users");
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.users = (com.geekbrains.server.auth.inner_auth.Users) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void saveUsers(){
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(Settings.SAVED_USERS_FOLDER);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(users);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
