package com.pingpong.standard;

import com.pingpong.AllocationTracker;
import com.pingpong.gc_free.custom.ByteUtil;
import com.pingpong.standard.client.StandardNioClient;
import com.pingpong.standard.server.StandardNioServer;
import org.junit.jupiter.api.Test;

import static com.pingpong.ConnectionInfo.PING;
import static com.pingpong.ConnectionInfo.PONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StandardAllocationTest {

    /**
     * Run this with VM option:
     * -javaagent:lib/java-allocation-instrumenter-3.2.0.jar
     * or
     * -javaagent:lib/java-allocation-instrumenter-3.0.jar
     * */

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
        StandardNioServer server = new StandardNioServer(messagesAmount);
        StandardNioClient client = new StandardNioClient(messagesAmount);

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
        assertNotEquals(0, AllocationTracker.getNumOfRecordedAllocations());
        assertEquals(0, AllocationTracker.getNumOfRecordedAllocations());
    }
}
