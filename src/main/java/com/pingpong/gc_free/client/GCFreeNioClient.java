package com.pingpong.gc_free.client;

import com.pingpong.gc_free.AllocationTracker;
import com.pingpong.gc_free.ByteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.pingpong.ConnectionInfo.*;

public class GCFreeNioClient extends Thread {

    private int messagetAmount;
    private String message;
    private int clients;
    private static int currentClient = 0;

//    private SocketChannel client;
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);

    private int sent = 0;
    private int got = 0;

    public GCFreeNioClient(int messagetAmount, String message, int clients) {
        this.messagetAmount = messagetAmount;
        this.message = message;
        this.clients = clients;
    }


    public void run() {
        try {
            startMessageExcange(messagetAmount, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMessageExcange(int size, String message) throws IOException {
        while (currentClient < clients) {
            try {
                System.out.println("Start new client");
                System.out.println(currentClient);
                System.out.println(clients);
                SocketChannel client = SocketChannel.open(new InetSocketAddress(HOST, PORT));
//                client.write(ByteBuffer.allocate(BUFFER_SIZE));
//                client.read(inputBuffer);
//                clearBuffers();
//                if (AllocationTracker.IS_ACTIVE) {
//                    AllocationTracker.clear();
//                    AllocationTracker.turnOn();
//                }

                for (int i = 0; i < size; i++) {
                    outputBuffer.put(byteUtil.bytesFromString(message));
                    outputBuffer.flip();
                    client.write(outputBuffer); sent++;
                    client.read(inputBuffer); got++;
                    clearBuffers();
                }

//                if (AllocationTracker.IS_ACTIVE) {
//                    AllocationTracker.turnOff();
//                }
                client.close();
                currentClient++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(currentClient);
    }


    private void clearBuffers() {
        inputBuffer.clear();
        outputBuffer.clear();
    }


    public int getMessagetAmount() {
        return messagetAmount;
    }

    public GCFreeNioClient setMessagetAmount(int messagetAmount) {
        this.messagetAmount = messagetAmount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public GCFreeNioClient setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getSent() {
        return sent;
    }

    public int getGot() {
        return got;
    }
}

