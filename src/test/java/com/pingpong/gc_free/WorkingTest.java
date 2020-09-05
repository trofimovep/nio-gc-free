package com.pingpong.gc_free;

import com.pingpong.gc_free.client.GCFreeNioClient;
import com.pingpong.gc_free.server.GCFreeNioServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkingTest {

    @Test
    public void test() throws InterruptedException {
        // given
        int messagesAmount = 10;
        int clients = 10;
        GCFreeNioServer server = new GCFreeNioServer(messagesAmount, PONG);
        GCFreeNioClient client = new GCFreeNioClient(messagesAmount, PING);

        //when
        server.start();
        Thread.sleep(1_000);
        client.start();

        client.join();
        server.join();

        // then
        Assertions.assertAll(
                () -> assertEquals(messagesAmount, client.getSent()),
                () -> assertEquals(messagesAmount, client.getGot()),
                () -> assertEquals(messagesAmount + 1, server.getSent()),
                () -> assertEquals(messagesAmount + 1, server.getGot())
        );
    }

}
