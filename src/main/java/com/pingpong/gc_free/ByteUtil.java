package com.pingpong.gc_free;

public class ByteUtil {

    private byte[] buffer;

    public ByteUtil(int size) {
        buffer = new byte[size];
    }

    public byte[] bytesFromString(String str) {
        for (int i = 0; i < str.length(); i++)
            buffer[i] = (byte) str.charAt(i);
        return buffer;
    }

}
