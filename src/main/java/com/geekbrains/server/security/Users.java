package com.geekbrains.server.security;

import java.util.HashMap;
import java.util.Map;

public class Users {
    public static Map <String, UsersHandler> list;

    public Users() {
        list = new HashMap<>();
    }

    public UsersHandler getUserHandlerByLogin(String login){
        return list.get(login);
    }


}


