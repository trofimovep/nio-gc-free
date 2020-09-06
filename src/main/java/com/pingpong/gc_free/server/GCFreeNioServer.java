package com.pingpong.gc_free.server;

import com.pingpong.AllocationTracker;
import com.pingpong.ConnectionInfo;
import com.pingpong.gc_free.custom.ByteUtil;
import com.pingpong.gc_free.custom.CustomSetUtil;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.impl.LinuxJNAAffinity;

//import openhft.affinity.impl.LinuxJNAAffinity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.List;

import static com.pingpong.ConnectionInfo.*;

public class  GCFreeNioServer extends Thread {

    private int limitMessages;
    private boolean isConnect = true;

    private int sent = 0;
    private int got = 0;
    private List<String> messages;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private ByteUtil byteUtil = new ByteUtil(BUFFER_SIZE);
    private CustomSetUtil customSetUtil = new CustomSetUtil();

    private SocketChannel client;
    private static Selector selector;
    private ServerSocket serverSocket;
    private SocketChannel anotheClient;
    private ServerSocketChannel socketChannel;
    private THashSet<SelectionKey> selectedKeys;
    private InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);


    private TObjectProcedure<SelectionKey> procedure = key -> {
        if (key.isAcceptable()) handleAccept(socketChannel);
        else if (key.isReadable()) handleRead(key);
        return true;
    };

    public GCFreeNioServer(int limitMessages) {
        this.limitMessages = limitMessages;
        this.messages = ConnectionInfo.generateRandomStrings(limitMessages + 1);
    }


    @Override
    public void run() {
        startServer();
    }


    private void startServer() {
        BitSet aff = new BitSet();
        aff.set(3, true);
        LinuxJNAAffinity.INSTANCE.setAffinity(aff);
        try {
//            try (AffinityLock al = AffinityLock.acquireCore()) {
                selector = Selector.open();
                customSetUtil.substitudeSelectedKeysSet(selector);

                socketChannel = ServerSocketChannel.open();
                serverSocket = socketChannel.socket();
                serverSocket.bind(inetSocketAddress);

                socketChannel.configureBlocking(false);
                socketChannel.register(selector, socketChannel.validOps(), null);


                // do some work while locked to a CPU.
                while (isConnect) {
//                BitSet aff = new BitSet();
//                aff.set(3, true);
//                LinuxJNAAffinity.INSTANCE.setAffinity(aff);
//                AffinityLock

//                    AffinityLock affinityLock

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
//    }


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
            outputBuffer.put(byteUtil.bytesFromString(messages.get(sent))).flip();
            anotheClient.write(outputBuffer); sent++;
            clearBuffers();
            if (sent == limitMessages + 1) {
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
