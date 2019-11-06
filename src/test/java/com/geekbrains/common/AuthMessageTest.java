package com.geekbrains.common;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthMessageTest {

    AuthMessage authMessage = new AuthMessage("ivan", "123");

    @Test
    public void getType() {
        Assert.assertEquals(Commands.MessageType.AUTH, authMessage.getType());
    }

    @Test
    public void getData() {
        Assert.assertEquals("ivan 123", authMessage.getData());
    }

    @Test
    public void getLogin() {
        Assert.assertEquals("ivan", authMessage.getLogin());
    }

    @Test
    public void getPassword() {
        Assert.assertEquals("123", authMessage.getPassword());
    }
}