package org.egbers.dirt.rally.client.model;

public class PacketVersion1 implements Packet {
    public PacketVersion1(byte[] rawData) {

    }

    public int getPacketType() {
        return 1;
    }
}
