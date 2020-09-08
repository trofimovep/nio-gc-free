package com.pingpong.gc_free;

import com.pingpong.AllocationTracker;
import com.pingpong.gc_free.client.GCFreeNioClient;
import com.pingpong.gc_free.custom.ByteUtil;
import com.pingpong.gc_free.server.GCFreeNioServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GCFreeAllocationTest {

    /**
     * Run this with VM option:
     * -javaagent:lib/java-allocation-instrumenter-3.2.0.jar
     * or
     * -javaagent:lib/java-allocation-instrumenter-3.0.jar
     * */

    @BeforeEach
    void turnOnAllocator() {
        AllocationTracker.IS_ACTIVE = true;
    }

    @Test
    public void test_agent_is_on() {
        // given
        AllocationTracker.clear();
        AllocationTracker.turnOn();

        //when
        byte[] bytes = new byte[10];

        //then
        AllocationTracker.turnOff();

        int numOfRecordedAllocations = AllocationTracker.getNumOfRecordedAllocations();
        assertEquals(1, numOfRecordedAllocations);
        AllocationTracker.clear();
        assertEquals(0, AllocationTracker.getNumOfRecordedAllocations());
    }


    @Test
    public void byteUtilAllocationTest() {
        // given
        ByteUtil byteUtil = new ByteUtil(20);
        String example = "example";
        AllocationTracker.clear();
        AllocationTracker.turnOn();

        // when
        byteUtil.bytesFromString(example);
        AllocationTracker.turnOff();

        // then
        AllocationTracker.clear();
        assertEquals(0, AllocationTracker.getNumOfRecordedAllocations());
    }


    @Test
    public void test() throws InterruptedException {
        // given
        int messagesAmount = 10_000;
        GCFreeNioServer server = new GCFreeNioServer(messagesAmount);
        GCFreeNioClient client = new GCFreeNioClient(messagesAmount);

        //when
        server.start();
        Thread.sleep(1_000);
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

        System.out.println(AllocationTracker.getAllocationsInfo());
        System.out.println("=============================================");
        assertEquals(0, AllocationTracker.getNumOfRecordedAllocations());
    }
}
