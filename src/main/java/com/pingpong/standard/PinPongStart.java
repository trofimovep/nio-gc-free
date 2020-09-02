package com.pingpong.standard;

import com.pingpong.gc_free.client.GCFreeNioClient;
import com.pingpong.gc_free.server.GCFreeNioServer;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;

public class PinPongStart {

    private static int limitMessages = 100;
    private static int clientsAmount = 100;

    private static GCFreeNioServer server = new GCFreeNioServer(limitMessages, PONG);
    private static GCFreeNioClient client = new GCFreeNioClient(limitMessages, PING, clientsAmount);

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
