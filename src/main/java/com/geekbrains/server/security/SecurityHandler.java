package com.geekbrains.server.security;

import com.geekbrains.common.Settings;
import com.geekbrains.common.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SecurityHandler {
    private User currentUser;
    private static Path userFolder;

    public SecurityHandler(User currentUser) {
        this.currentUser = currentUser;
        userFolder = Paths.get(Settings.SERVER_FOLDER + currentUser.getLogin());
    }

    boolean checkPermission(String fileName){
        return (Files.exists(Paths.get(userFolder + fileName)));
    }

    public static Path getUserFolder() {
        return userFolder;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void createUserFolder(User user) {
        if (!Files.exists(Paths.get(Settings.SERVER_FOLDER + user.getLogin()))){
            try {
                Files.createDirectory(Paths.get(Settings.SERVER_FOLDER + user.getLogin()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println(Paths.get(Settings.SERVER_FOLDER + user.getLogin()) + " already exist");
    }
}
