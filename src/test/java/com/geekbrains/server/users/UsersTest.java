package com.geekbrains.server.users;

import com.geekbrains.common.User;
import org.junit.Assert;
import org.junit.Test;

public class UsersTest {
    private Users users = new Users();
    @Test
    public void addToAllUser() {
        users.addToAllUserAndReturnID(new User(-1, "ivan", "123"));
        users.addToAllUserAndReturnID(new User(-1, "sdfivan", "123"));
        users.addToAllUserAndReturnID(new User(-1, "ivsfan", "123"));
        users.addToAllUserAndReturnID(new User(-1, "ivddan", "123"));

        Assert.assertEquals(2, users.addToAllUserAndReturnID(new User(-1, "1223", "123")));
    }
}