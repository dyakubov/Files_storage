package com.geekbrains.server.auth.inner_auth;

import com.geekbrains.common.User;

import java.io.Serializable;
import java.util.*;
import java.util.function.IntSupplier;

public class Users implements Serializable {
    private Map<String, User > allUsers;

    public Users(){
        allUsers = new HashMap<>();
    }

    public Map<String, User> getAllUsers(){
        return allUsers;
    }

    public void addToAllUsers(User user){
        allUsers.put(user.getLogin(), user);
    }
}
