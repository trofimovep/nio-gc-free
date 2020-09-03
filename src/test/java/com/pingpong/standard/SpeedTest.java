package com.pingpong.standard;

import com.pingpong.standard.client.StandardNioClient;
import com.pingpong.standard.server.StandardNioServer;
import org.junit.jupiter.api.Test;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;


public class SpeedTest {

    @Test
    public void test() throws InterruptedException {
        // given
        int messagesAmount = 10_000;
        StandardNioServer server = new StandardNioServer(messagesAmount, PONG);
        StandardNioClient client = new StandardNioClient(messagesAmount, PING);

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
