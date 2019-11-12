package com.geekbrains.common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private int id;
    private String login;

    public User( int id, String login) {
        this.id = id;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
