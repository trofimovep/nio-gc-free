package com.pingpong;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionInfo {
    public static final String HOST = "localhost";
    public static final int PORT = 8081;

    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String EXIT = "exit";

    public static final int BUFFER_SIZE = 1024;

    public static List<String> generateRandomStrings(int amount) {
        List strings = new ArrayList(amount);
        for (int i = 0; i < amount; i++)
            strings.add(UUID.randomUUID().toString());
        return strings;
    }
}
