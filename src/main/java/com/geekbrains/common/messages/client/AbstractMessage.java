package com.geekbrains.common.messages.client;

import com.geekbrains.common.User;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;
    private User user;

    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
