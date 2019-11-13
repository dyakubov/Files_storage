package com.geekbrains.common.messages.server;
import java.util.List;

public class FilesList extends ServerMessage {
    private List<String> allFiles;
    public FilesList(ServerAnswerType serverAnswerType, List<String> allFiles) {
        super(serverAnswerType);
        this.allFiles = allFiles;
    }

    public List<String> getAllFiles() {
        return allFiles;
    }
}
