package com.pingpong;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;

public class AllocationTracker {

    public static final boolean IS_ACTIVE = true;

    private static boolean[] activated = new boolean[1];

    private static int numOfRecordedAllocations = 0;

    private static StringBuilder amountByTypes = new StringBuilder();
    private static int stringAmount = 0;
    private static int intAmount = 0;
    private static int byteAmount = 0;
    private static int charAmount = 0;
    private static int longAmount = 0;
    private static int hashMapAmount = 0;
    private static int arrayListAmount = 0;
    private static int ofAllocateTracker = 0;
    private static int otherAmount = 0;


    private static Sampler sampler = (count, desc, newObj, size) -> {

        if (!activated[0]) return;

        turnOff();

        numOfRecordedAllocations++;
        System.out.print("Num of recorded allocations ");
        System.out.println(numOfRecordedAllocations);

        new Exception().printStackTrace(System.out);

        System.out.print("allocated object [");
        System.out.print(newObj.getClass().getName());
        System.out.print("] of type [");
        System.out.print(desc);
        System.out.print("] whose size is [");
        System.out.print(size);
        if (count != -1) {
            System.out.print("] it's an array of size [");
            System.out.print(count);
        }
        System.out.print("]");
        System.out.println();

        countOfType(desc);
        turnOn();
    };

    static {
        AllocationRecorder.addSampler(sampler);
    }

    public static void turnOn() {
        activated[0] = true;
    }

    public static void turnOff() {
        activated[0] = false;
    }

    public static void clear() {
        numOfRecordedAllocations = 0;
        stringAmount = 0;
        intAmount = 0;
        byteAmount = 0;
        charAmount = 0;
        longAmount = 0;
        hashMapAmount = 0;
        arrayListAmount = 0;
        ofAllocateTracker = 0;
        otherAmount = 0;
    }

    public static void clearAndTurnOn() {
        clear();
        turnOn();
    }

    public static int getNumOfRecordedAllocations() {
        return numOfRecordedAllocations;
    }

    private static void countOfType(String desc) {
        if (desc.contains("com/google/monitoring/runtime/instrumentation/")) ofAllocateTracker++;
        else if (desc.contains("String")) stringAmount++;
        else if (desc.contains("int")) intAmount++;
        else if (desc.contains("byte")) byteAmount++;
        else if (desc.contains("char")) charAmount++;
        else if (desc.contains("long")) longAmount++;
        else if (desc.contains("HashMap")) hashMapAmount++;
        else if (desc.contains("ArrayList")) arrayListAmount++;
        else otherAmount++;
    }

    public static String getAllocationsInfo() {
        amountByTypes.append("\nCommon Allocation Amount: " + numOfRecordedAllocations)
        .append("\n_________________________________________________________\n")
        .append("Amount of types: \n")
        .append("Strings (or String array): ").append(stringAmount).append("\n")
        .append("Integer(or int array): ").append(intAmount).append("\n")
        .append("Byte(or byte array): ").append(byteAmount).append("\n")
        .append("Character(or char array): ").append(charAmount).append("\n")
        .append("Long(or long array): ").append(longAmount).append("\n")
        .append("HashMap: ").append(hashMapAmount).append("\n")
        .append("ArrayList: ").append(arrayListAmount).append("\n")
        .append("Ammount of Google AllocationInstrumenter: ").append(ofAllocateTracker).append("\n")
        .append("Others: ").append(otherAmount).append("\n");
        return amountByTypes.toString();
    }


}
