package com.geekbrains.server.server;

import com.geekbrains.common.Commands;
import org.junit.Assert;
import org.junit.Test;

public class ProtocolHandlerTest {
    ProtocolHandler protocolHandler = new ProtocolHandler();
    @Test
    public void makeCommand() {
        Assert.assertEquals((Commands.START_IDENT + Commands.SEPARATOR + Commands.MessageType.AUTH + Commands.SEPARATOR) + "ivan" + Commands.SEPARATOR + "123" + Commands.SEPARATOR + Commands.END_IDENT
                , protocolHandler.compileMessage(Commands.MessageType.AUTH, "ivan 123"));

        System.out.println(protocolHandler.compileMessage(Commands.MessageType.AUTH, "ivan 123"));
    }
}