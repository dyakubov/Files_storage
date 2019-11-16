package com.geekbrains.common.messages.server;
import java.util.List;

public class FilesList extends ServerMessage {
    private List<String> allFiles;
    public FilesList(List<String> allFiles) {
        super(ServerAnswerType.FILE_LIST);
        this.allFiles = allFiles;
    }

    public List<String> getAllFiles() {
        return allFiles;
    }
}
