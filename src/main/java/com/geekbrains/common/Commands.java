package com.geekbrains.common;

public final class Commands{
    public static final String START_IDENT = "_START";
    public static final String END_IDENT = "_END";
    public static final String SEPARATOR = "___";
    private static final String OK = "_OK";
    private static final String FAIL = "_FAIL";


    public static final String AUTH_OK = MessageType.AUTH + OK;
    public static final String AUTH_FAIL = MessageType.AUTH + FAIL;
    public static final String REG_OK = MessageType.REG + OK;
    public static final String REG_FAIL = MessageType.REG + FAIL;



    public static final String EMPTY = "-1";

    public final static class MessageType{
        public static final String AUTH = "AUTH";
        public static final String REG = "REG";
        public static final String GET_FILE = "GET_FILE";
        public static final String DELETE_FILE = "DEL";
        public static final String RENAME_FILE = "RENAME";
        public static final String UNKNOWN_MESSAGE = "UNKNOWN";
    }
}
