package com.geekbrains.server.security;

import com.geekbrains.common.Settings;
import com.geekbrains.common.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SecurityHandler {
    private User currentUser;
    private Path userFolder;

    public SecurityHandler(User currentUser) {
        this.currentUser = currentUser;
        createUserFolder();
    }

    private void createUserFolder() {
        Path p = Paths.get(Settings.SERVER_FOLDER + currentUser.getLogin());

        if (!Files.exists(p)) {
            try {
                Files.createDirectory(p);
                userFolder = p;
            } catch (IOException e) {
                System.out.println(p + " already exist");
            }
        } else {
            userFolder = p;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Path getUserFolder() {
        return userFolder;
    }
}
