package com.pingpong.standard.client;

import com.pingpong.AllocationTracker;
import com.pingpong.ConnectionInfo;
import com.pingpong.gc_free.custom.ByteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import static com.pingpong.ConnectionInfo.*;

public class StandardNioClient extends Thread {

    private int messagetAmount;
    private List<String> messages;

    private SocketChannel client;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private int sent = 0;
    private int got = 0;

    public StandardNioClient(int messagetAmount) {
        this.messagetAmount = messagetAmount;
        this.messages= ConnectionInfo.generateRandomStrings(messagetAmount);
    }


    public void run() {
        try {
            startMessageExcange(messagetAmount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMessageExcange(int size) throws IOException {
        try {
            client = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            // sent first for creating a buffer cache
            client.write(ByteBuffer.allocate(BUFFER_SIZE));
            client.read(inputBuffer);
            clearBuffers();

            if (AllocationTracker.IS_ACTIVE) {
                AllocationTracker.clearAndTurnOn();
            }

            for (int i = 0; i < size; i++) {
                outputBuffer.put(messages.get(i).getBytes());
                outputBuffer.flip();
                client.write(outputBuffer); sent++;
                client.read(inputBuffer); got++;
                clearBuffers();
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

    public List<String> getMessage() {
        return messages;
    }

    public StandardNioClient setMessage(List<String> messages) {
        this.messages = messages;
        return this;
    }

    public int getSent() {
        return sent;
    }

    public int getGot() {
        return got;
    }
}

