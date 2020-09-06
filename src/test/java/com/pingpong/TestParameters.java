package com.pingpong;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestParameters {

    private static final String STATS_FOLDER = "src/main/stats/";
    public static final int EXPERIMENTS_AMOUNT = 1;
    public static List<Integer> MESSAGES_AMOUNT = Arrays.asList(1, 10, 100, 200, 300, 1_000, 10_000, 100_000, 1_000_000, 2_000_000, 3_000_000, 3_500_000, 4_000_000, 5_000_000,6_000_000);

    public static void writeStat(String filename, long[] stats) {
        try (FileWriter file = new FileWriter(STATS_FOLDER + filename)) {
            StringBuilder builder = new StringBuilder();
            for (long stat : stats) {
                builder.append(stat).append(";");
            }
            file.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
