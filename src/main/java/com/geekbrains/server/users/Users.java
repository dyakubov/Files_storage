package com.geekbrains.server.users;

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

    public int addToAllUserAndReturnID(User user){
        int lastID = Arrays.stream(allUsers.entrySet().toArray()).mapToInt(v -> user.getId()).max().orElseGet(()->0);
        user.setId(++lastID);
        allUsers.put(user.getLogin(), user);
        return lastID;
    }

    public User getUserById(int id){
        return allUsers.get(id);
    }
}
