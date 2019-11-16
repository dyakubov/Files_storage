package com.geekbrains.common.messages.server;

public enum ServerAnswerType {
    AUTH_OK,
    AUTH_FAILED,
    REG_OK,
    USER_ALREADY_EXIST,
    ACCESS_DENIED,
    USER_NOT_FOUND,
    FILE_NOT_FOUND,
    FILE_LIST,
    DELETED,
    RENAMED,
    RENAME_FAILED
}
