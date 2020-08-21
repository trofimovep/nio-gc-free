package com.pingpong.threadable;

import com.pingpong.threadable.client.StandardNioClient;
import com.pingpong.threadable.server.StandardNioServer;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;

public class PinPongStart {

    private static int limitMessages = 100;
    private static StandardNioServer server = new StandardNioServer(limitMessages, PONG);
    private static StandardNioClient client = new StandardNioClient(limitMessages, PING);

    public static void main(String[] args) throws InterruptedException {
        server.start();
        Thread.sleep(1000);
        client.start();

        client.join();
        server.join();

        System.out.println("Sent by client: " + client.getSent());
        System.out.println("Recieved by client: " + client.getGot());

        System.out.println("Sent by server: " + server.getSent());
        System.out.println("Recieved by server: " + server.getGot());
    }

}
