package com.pingpong;

import java.nio.ByteBuffer;

public class ConnectionInfo {
    public static final String HOST = "localhost";
    public static final int PORT = 8081;

    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String EXIT = "exit";

    public static final byte[] EXIT_BYTES = EXIT.getBytes();
    public static final ByteBuffer EXIT_BUFFER = ByteBuffer.wrap(EXIT_BYTES);
}
