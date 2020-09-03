package com.pingpong;

import com.pingpong.gc_free.GetStatisticGCFreeNioTest;
import com.pingpong.standard.GetStatisticStandardNioTest;
import org.junit.jupiter.api.Test;

public class GetStatTest {

    @Test
    public void getStat() throws InterruptedException {
        System.out.println("Starting getting statistic...");

        long start = System.nanoTime();
        GetStatisticStandardNioTest getStatisticStandardNioTest = new GetStatisticStandardNioTest();
        getStatisticStandardNioTest.test();

        System.gc();

        GetStatisticGCFreeNioTest getStatisticGCFreeNioTest = new GetStatisticGCFreeNioTest();
        getStatisticGCFreeNioTest.test();
        long end = System.nanoTime();

        System.out.println("The getting statistic finished in " + (end - start) + " ns");
    }
}