package com.geekbrains.server.security;

import java.util.HashMap;
import java.util.Map;

public class SecurityHandlers {
    public static Map <String, SecurityHandler> list;

    public SecurityHandlers() {
        list = new HashMap<>();
    }

    public SecurityHandler getSecurityHandlerByLogin(String login){
        return list.get(login);
    }


}


