package com.pingpong.gc_free;

import com.pingpong.AllocationTracker;
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
        AllocationTracker.IS_ACTIVE = false;
        long[] stats = new long[MESSAGES_AMOUNT.size()];

        for (Integer messageAmount : MESSAGES_AMOUNT) {
            long currentStat = 0;
            for (int j = 0; j < TestParameters.EXPERIMENTS_AMOUNT; j++) {
                GCFreeNioServer server = new GCFreeNioServer(messageAmount);
                GCFreeNioClient client = new GCFreeNioClient(messageAmount);

                server.start();
                System.gc();
                Thread.sleep(300);

                long start = System.nanoTime();
                client.start();
                client.join();
                server.join();
                long end = System.nanoTime();
                currentStat += end - start;
            }
            currentStat = currentStat / TestParameters.EXPERIMENTS_AMOUNT;
            stats[MESSAGES_AMOUNT.indexOf(messageAmount)] = currentStat;
            System.out.println("Got stat for " + messageAmount + " message amount (" + TestParameters.EXPERIMENTS_AMOUNT + " experiments): " + currentStat);
        }
        TestParameters.writeStat("GCFreeNioStat.txt", stats);
    }
}
