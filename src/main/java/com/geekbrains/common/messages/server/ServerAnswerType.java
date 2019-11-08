package com.geekbrains.common.messages.server;

public enum ServerAnswerType {
    AUTH_OK,
    AUTH_FAILED,
    REG_OK,
    REG_FAILED,
    ACCESS_DENIED,
    USER_ALREADY_EXIST,
    USER_NOT_FOUND,
    UNKNOWN_COMMAND,
    FILE,
    FILE_NOT_FOUND,
    FILE_LIST,
    DELETED,
    RENAMED
}
