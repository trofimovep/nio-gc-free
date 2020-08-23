package com.pingpong.threadable.client;

import com.pingpong.AllocationTracker;
import com.pingpong.ByteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.pingpong.ConnectionInfo.*;

public class StandardNioClient extends Thread {

    private int messagetAmount;
    private String message;

    private SocketChannel client;
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);

    private int sent = 0;
    private int got = 0;

    public StandardNioClient(int messagetAmount, String message) {
        this.messagetAmount = messagetAmount;
        this.message = message;
    }


    public void run() {
        try {
            startMessageExcange(messagetAmount, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client is dying...");
    }

    private void startMessageExcange(int size, String message) throws IOException {
        try {
            client = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            // sent first for creating a buffer cache
            client.write(ByteBuffer.allocate(BUFFER_SIZE));
            client.read(inputBuffer);
            clearBuffers();
            if (AllocationTracker.IS_ACTIVE) {
                AllocationTracker.clear();
                AllocationTracker.turnOn();
            }

            for (int i = 0; i < size; i++) {
                outputBuffer.put(byteUtil.bytesFromString(message));
                outputBuffer.flip();
                client.write(outputBuffer); sent++;
                client.read(inputBuffer); got++;
                clearBuffers();
            }

            if (AllocationTracker.IS_ACTIVE) {
                AllocationTracker.turnOff();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }


    private void clearBuffers() {
        inputBuffer.clear();
        outputBuffer.clear();
    }


    public int getMessagetAmount() {
        return messagetAmount;
    }

    public StandardNioClient setMessagetAmount(int messagetAmount) {
        this.messagetAmount = messagetAmount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StandardNioClient setMessage(String message) {
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

