package com.geekbrains.server.server;

import java.util.HashMap;
import java.util.Map;

public class Users {
    Map<String, String > users;

    public Users(){
        users = new HashMap<>();
        users.put("ivan", "123");

    }

    public Map<String, String> getUsers(){
        return users;
    }

}
