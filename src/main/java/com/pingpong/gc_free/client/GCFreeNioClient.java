package com.pingpong.gc_free.client;

import com.pingpong.AllocationTracker;
import com.pingpong.gc_free.custom.ByteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.pingpong.ConnectionInfo.*;

public class GCFreeNioClient extends Thread {

    private int messagetAmount;
    private String message;

    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);

    private SocketChannel client;
    private int sent = 0;
    private int got = 0;

    public GCFreeNioClient(int messagetAmount, String message) {
        this.messagetAmount = messagetAmount;
        this.message = message;
    }


    public void run() {
        try {
            startMessageExcange(messagetAmount, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMessageExcange(int size, String message) throws IOException {
                client = SocketChannel.open(new InetSocketAddress(HOST, PORT));
                client.write(ByteBuffer.allocate(BUFFER_SIZE));
                client.read(inputBuffer);
                clearBuffers();

                if (AllocationTracker.IS_ACTIVE) {
                    AllocationTracker.clearAndTurnOn();
                }

                for (int i = 0; i < size; i++) {
                    outputBuffer.put(byteUtil.bytesFromString(message));
                    outputBuffer.flip();
                    client.write(outputBuffer); sent++;
                    client.read(inputBuffer); got++;
                    clearBuffers();
                }
                client.close();
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

