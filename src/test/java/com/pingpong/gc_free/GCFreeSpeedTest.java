package com.pingpong.gc_free;

import com.pingpong.gc_free.client.GCFreeNioClient;
import com.pingpong.gc_free.server.GCFreeNioServer;
import org.junit.jupiter.api.Test;

public class GCFreeSpeedTest {

    @Test
    public void test() throws InterruptedException {
        // given
        int messagesAmount = 10_000;
        GCFreeNioServer server = new GCFreeNioServer(messagesAmount);
        GCFreeNioClient client = new GCFreeNioClient(messagesAmount);

        //when
        server.start();
        Thread.sleep(1000);

        long start = System.nanoTime();
        client.start();
        client.join();
        server.join();
        long end = System.nanoTime();

        long time = end - start;
        System.out.println("==================================================");
        System.out.println("Messages sent: " + messagesAmount);
        System.out.println("The needed time: " + time + " ns");
        System.out.println("The avarage time: " + time / messagesAmount + " ns");
        System.out.println("==================================================");
    }
}
