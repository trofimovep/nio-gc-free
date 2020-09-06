package com.pingpong.standard.server;

import com.pingpong.AllocationTracker;
import com.pingpong.ConnectionInfo;
import com.pingpong.gc_free.custom.ByteUtil;
import com.pingpong.gc_free.custom.CustomSetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Set;

import static com.pingpong.ConnectionInfo.*;

public class StandardNioServer extends Thread {

    private int limitMessages;
    private boolean isConnect = true;

    private int sent = 0;
    private int got = 0;
    private List<String> messages;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private int ops;
    private SocketChannel client;
    private static Selector selector;
    private ServerSocket serverSocket;
    private Set<SelectionKey> selectedKeys;
    private ServerSocketChannel socketChannel;
    private InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);


    public StandardNioServer(int limitMessages) {
        this.limitMessages = limitMessages;
        this.messages = ConnectionInfo.generateRandomStrings(limitMessages + 1);
    }


    @Override
    public void run() {
        startServer();
    }


    private void startServer() {
        try {
            selector = Selector.open();

            socketChannel = ServerSocketChannel.open();
            serverSocket = socketChannel.socket();
            serverSocket.bind(inetSocketAddress);

            socketChannel.configureBlocking(false);
            ops = socketChannel.validOps();
            socketChannel.register(selector, ops, null);

            while (isConnect) {
                try {
                    selector.select();
                    selectedKeys = selector.selectedKeys();
                    selectedKeys.forEach(selectionKey -> {
                        if (selectionKey.isAcceptable()) handleAccept(socketChannel);
                        else if (selectionKey.isReadable()) handleRead(selectionKey);
                    });
                    selectedKeys.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleAccept(ServerSocketChannel mySocket) {
        try {
            client = mySocket.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) {
        try {
            client = (SocketChannel) key.channel();
            client.read(inputBuffer); got++;
            outputBuffer.put(messages.get(sent).getBytes()).flip();
            client.write(outputBuffer); sent++;
            clearBuffers();
            if (sent == limitMessages + 1) {
                if (AllocationTracker.IS_ACTIVE) AllocationTracker.turnOff();
                isConnect = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearBuffers();
    }


    private void clearBuffers() {
        inputBuffer.clear();
        outputBuffer.clear();
    }

    public int getSent() {
        return sent;
    }

    public int getGot() {
        return got;
    }

}
