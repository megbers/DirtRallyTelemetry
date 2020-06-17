package org.egbers.dirt.rally.client.model;

public class PacketFactory {
    public static final int TYPE_0 = 0;
    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;

    public static Packet createPacket(int type, byte[] rawData) {
        if(type == TYPE_0) {
            return new PacketVersion0(rawData);
        } else if(type == TYPE_1) {
            return new PacketVersion1(rawData);
        } else if(type == TYPE_2) {
            return new PacketVersion2(rawData);
        } else {
            return new PacketVersion3(rawData);
        }
    }
}
