package com.geekbrains.common;

public class ServiceMessage implements AbstractMessage  {

    private String type;
    private String[] data;

    public ServiceMessage(String type, String[] data){
        if (type.equals(Commands.MessageType.GET_FILE)){
            this.type = type;
        } else if (type.equals(Commands.MessageType.DELETE_FILE)){
            this.type = type;
        } else if (type.equals(Commands.MessageType.RENAME_FILE)){
            this.type = type;
        } else this.type = Commands.MessageType.UNKNOWN_MESSAGE;
        this.data = data;
    }
    @Override
    public String getType() {
        return type;
    }

    @Override
    public String[] getData() {
        return data;
    }
}
