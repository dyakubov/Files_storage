package com.geekbrains.common.messages.client;

import com.geekbrains.common.User;

import java.io.Serializable;

public class ServiceMessage implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;

    private ServiceMessageType serviceMessageType;
    private User user;
    private String body;

    public ServiceMessage(ServiceMessageType serviceMessageType, User user, String body) {
        this.serviceMessageType = serviceMessageType;
        this.user = user;
        this.body = body;
    }

    public ServiceMessage(ServiceMessageType serviceMessageType, User user) {
        this.serviceMessageType = serviceMessageType;
        this.user = user;
    }

    public ServiceMessage() {
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public ServiceMessageType getServiceMessageType() {
        return serviceMessageType;
    }


    public void setServiceMessageType(ServiceMessageType serviceMessageType) {
        this.serviceMessageType = serviceMessageType;
    }
}
