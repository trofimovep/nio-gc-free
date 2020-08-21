package com.pingpong.threadable.server;

import com.pingpong.AllocationTracker;
import com.pingpong.ByteUtil;
import com.pingpong.CustomSetUtil;
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

import static com.pingpong.ConnectionInfo.HOST;
import static com.pingpong.ConnectionInfo.PORT;

public class StandardNioServer extends Thread {

    private int limitMessages;
    private boolean isConnect = true;

    private String outputMessage;
    private int sent = 0;
    private int got = 0;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(1024);

    private ByteUtil byteUtil = new ByteUtil(1024);
    private CustomSetUtil customSetUtil;

    private static Selector selector;
    private ServerSocketChannel socketChannel;
    private ServerSocket serverSocket;
    private InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);
    private int ops;
    private THashSet<SelectionKey> selectedKeys;
    private SocketChannel client;
    private SocketChannel anotherClient;


    TObjectProcedure<SelectionKey> procedure = key -> {
        if (key.isAcceptable()) handleAccept(socketChannel);
        else if (key.isReadable()) handleRead(key);
        return true;
    };

    public StandardNioServer(int limitMessages, String outputMessage) {
        this.outputMessage = outputMessage;
        this.limitMessages = limitMessages;
        customSetUtil = new CustomSetUtil();
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
            anotherClient = (SocketChannel) key.channel();
            anotherClient.read(inputBuffer); got++;
            outputBuffer.put(byteUtil.bytesFromString(outputMessage)).flip();
            anotherClient.write(outputBuffer); sent++;
            if (limitMessages == sent) {
                if (AllocationTracker.IS_ACTIVE)
                    AllocationTracker.turnOff();
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
