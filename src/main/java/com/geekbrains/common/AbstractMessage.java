package com.geekbrains.common;

public interface AbstractMessage {
    byte getType();
    byte[] getAuthData();
}
