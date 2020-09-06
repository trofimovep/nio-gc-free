package com.pingpong.gc_free.client;

import com.pingpong.AllocationTracker;
import com.pingpong.ConnectionInfo;
import com.pingpong.gc_free.custom.ByteUtil;
import net.openhft.affinity.AffinityLock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import static com.pingpong.ConnectionInfo.*;

public class GCFreeNioClient extends Thread {

    private int messagetAmount;
    private List<String> messages;

    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);

    private SocketChannel client;
    private int sent = 0;
    private int got = 0;

    public GCFreeNioClient(int messagetAmount) {
        this.messagetAmount = messagetAmount;
        this.messages = ConnectionInfo.generateRandomStrings(messagetAmount);
    }


    public void run() {
        try {
            try (AffinityLock al = AffinityLock.acquireCore()) {
                startMessageExcange(messagetAmount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMessageExcange(int size) throws IOException {
                client = SocketChannel.open(new InetSocketAddress(HOST, PORT));
                client.write(ByteBuffer.allocate(BUFFER_SIZE));
                client.read(inputBuffer);
                clearBuffers();

                if (AllocationTracker.IS_ACTIVE) {
                    AllocationTracker.clearAndTurnOn();
                }

                for (int i = 0; i < size; i++) {
                    outputBuffer.put(byteUtil.bytesFromString(messages.get(i)));
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

    public List<String> getMessages() {
        return messages;
    }

    public GCFreeNioClient setMessages(List<String> messages) {
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

