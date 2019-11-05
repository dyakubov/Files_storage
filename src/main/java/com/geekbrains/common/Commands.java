package com.geekbrains.common;

public final class Commands{
    public static final String START_IDENT = "$START";
    public static final String END_IDENT = "$END";
    public static final String SEPARATOR = "$$$";
    private static final String OK = "_OK";
    private static final String FAIL = "_FAIL";


    public static final String AUTH_OK = MessageType.AUTH + OK;
    public static final String AUTH_FAIL = MessageType.AUTH + FAIL;
    public static final String REG_OK = MessageType.REG + OK;
    public static final String REG_FAIL = MessageType.REG + FAIL;



    public static final String EMPTY = "-1";

    public final static class MessageType{
        static final String AUTH = "AUTH";
        static final String REG = "REG";
        static final String GET_FILE = "GET_FILE";
        static final String DELETE_FILE = "DEL";
        static final String RENAME_FILE = "RENAME";
        static final String UNKNOWN_MESSAGE = "UNKNOWN";
    }


    private static String makeCommand(MessageType type, byte[] data){
        String result = START_IDENT + SEPARATOR + type;
        if (data.length == 0){
            return EMPTY;
        }
        for (byte b : data) {
            result = result.concat(String.valueOf(b)).concat(SEPARATOR);

        }
        return result.concat(END_IDENT);
    }

    private static String getMessageType(String data){
        if (!data.startsWith(START_IDENT)){
            return MessageType.UNKNOWN_MESSAGE;
        }

        return null;

    }
}
