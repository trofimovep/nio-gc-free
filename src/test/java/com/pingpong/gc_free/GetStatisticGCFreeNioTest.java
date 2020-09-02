package com.pingpong.gc_free;

import com.pingpong.TestParameters;
import com.pingpong.gc_free.client.GCFreeNioClient;
import com.pingpong.gc_free.server.GCFreeNioServer;
import com.pingpong.standard.client.StandardNioClient;
import com.pingpong.standard.server.StandardNioServer;
import org.junit.jupiter.api.Test;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;
import static com.pingpong.TestParameters.*;

public class GetStatisticGCFreeNioTest {

    @Test
    public void test() throws InterruptedException {
        System.out.println("Starting getting statistic for gc free nio...");

        GCFreeNioServer server;
        GCFreeNioClient client;
        long[] stats = new long[MESSAGES_AMOUNT.size()];

        for (Integer messageAmount : MESSAGES_AMOUNT) {
            long currentStat = 0;
            for (int j = 0; j < EXPERIMENTS_AMOUNT; j++) {
                server = new GCFreeNioServer(messageAmount, PONG);
                client = new GCFreeNioClient(messageAmount, PING, CLIENTS_AMOUNT);

                server.start();
                Thread.sleep(300);

                long start = System.nanoTime();
                client.start();
                client.join();
                server.join();
                long end = System.nanoTime();
                currentStat += end - start;
            }
            currentStat = currentStat / EXPERIMENTS_AMOUNT;
            stats[MESSAGES_AMOUNT.indexOf(messageAmount)] = currentStat;
            System.out.println("Got stat for " + messageAmount + " message amount (" + EXPERIMENTS_AMOUNT + " experiments): " + currentStat);
        }
        TestParameters.writeStat("GCFreeNioStat.txt", stats);
    }
}
