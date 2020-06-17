package org.egbers.dirt.rally.client.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacketVersion3 implements Packet {
    private List<Float> parsedData;

    public PacketVersion3(byte[] rawData) {
        parsedData = new ArrayList<>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
        while(byteBuffer.hasRemaining()) {
            parsedData.add(byteBuffer.getFloat());
        }
    }

    public int getPacketType() {
        return 3;
    }








}
