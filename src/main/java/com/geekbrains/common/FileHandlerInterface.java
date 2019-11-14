package com.geekbrains.common;

import com.geekbrains.client.console_client.handlers.ConsoleHandler;

import java.io.IOException;
import java.nio.file.Path;

public interface FileHandlerInterface {

    void downloadFile();
    void sendFile(Path path) throws IOException;
}
