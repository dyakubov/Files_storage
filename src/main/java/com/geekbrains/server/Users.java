package com.geekbrains.server;

import com.geekbrains.common.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Users {
    Map<String, String > allUsers;
    static List<User> authorizedUsers;

    public Users(){

        allUsers = new HashMap<>();
        authorizedUsers = new LinkedList<>();
    }

    public Map<String, String> getAllUsers(){
        return allUsers;
    }

    public void addToAllUser(String login, String pass){
        this.allUsers.put(login, pass);
    }

    public void addToAuthorizedUsers(User user){
        authorizedUsers.add(user);
    }

    public static List<User> getAuthorizedUsers() {
        return authorizedUsers;
    }
}
