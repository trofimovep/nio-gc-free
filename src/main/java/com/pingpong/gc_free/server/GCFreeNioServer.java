package com.pingpong.gc_free.server;

import com.pingpong.gc_free.AllocationTracker;
import com.pingpong.gc_free.ByteUtil;
import com.pingpong.gc_free.CustomSetUtil;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.pingpong.ConnectionInfo.*;

public class  GCFreeNioServer extends Thread {

    private int limitMessages;
    private boolean isConnect = true;

    private String outputMessage;
    private int sent = 0;
    private int got = 0;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);
    private CustomSetUtil customSetUtil = new CustomSetUtil();

    private static Selector selector;
    private ServerSocketChannel socketChannel;
    private ServerSocket serverSocket;
    private InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);
    private int ops;
    private THashSet<SelectionKey> selectedKeys;
    private SocketChannel client;
    private SocketChannel anotheClient;


    TObjectProcedure<SelectionKey> procedure = key -> {
        if (key.isAcceptable()) handleAccept(socketChannel);
        else if (key.isReadable()) handleRead(key);
        return true;
    };

    public GCFreeNioServer(int limitMessages, String outputMessage) {
        this.outputMessage = outputMessage;
        this.limitMessages = limitMessages;
    }


    @Override
    public void run() {
        startServer();
    }


    private void startServer() {
        try {
            selector = Selector.open();
            customSetUtil.substitudeSelectedKeysSet(selector);

            socketChannel = ServerSocketChannel.open();
            serverSocket = socketChannel.socket();
            serverSocket.bind(inetSocketAddress);

            socketChannel.configureBlocking(false);
            ops = socketChannel.validOps();
            socketChannel.register(selector, ops, null);

            while (isConnect) {
                try {
                    selector.select();
                    selectedKeys = (THashSet<SelectionKey>) selector.selectedKeys();
                    selectedKeys.forEach(procedure);
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
            anotheClient = (SocketChannel) key.channel();
            anotheClient.read(inputBuffer); got++;
            outputBuffer.put(byteUtil.bytesFromString(outputMessage)).flip();
            anotheClient.write(outputBuffer); sent++;
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