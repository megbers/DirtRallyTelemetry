package org.egbers.dirt.rally.client.model;

public class PacketVersion2 implements Packet{
    public PacketVersion2(byte[] rawData) {

    }

    public int getPacketType() {
        return 2;
    }
}
