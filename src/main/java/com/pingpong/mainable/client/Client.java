package com.pingpong.mainable.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.pingpong.ConnectionInfo.*;

public class Client {
    public static void main(String[] args) {
        try {
            String[] messages = {PING, PING, PING, PING, PING, "exit"};
            System.out.println("Starting client...");
            SocketChannel client = SocketChannel.open(new InetSocketAddress(HOST, PORT));

            for (String msg : messages) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(msg.getBytes());
                buffer.flip();
                int bytesWritten = client.write(buffer);

                ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
                client.read(inputBuffer);
                String data = new String(inputBuffer.array()).trim();

                System.out.println("Message recieved: " + data);
            }

            client.close();
            System.out.println("Client connection closed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

