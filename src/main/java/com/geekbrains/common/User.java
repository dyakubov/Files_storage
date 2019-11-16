package com.geekbrains.common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private int id;
    private String login;
    private String pass;

    public User( int id, String login, String pass) {
        this.id = id;
        this.login = login;
        this.pass = pass;
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

    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
}
