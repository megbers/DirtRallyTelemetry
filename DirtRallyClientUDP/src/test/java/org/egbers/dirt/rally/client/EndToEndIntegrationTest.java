package org.egbers.dirt.rally.client;

import org.egbers.dirt.rally.client.model.Packet;
import org.egbers.dirt.rally.client.model.PacketFactory;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Random;

public class EndToEndIntegrationTest {
    @Test
    public void shouldParseFloats() {
        float[] floats = new float[67];
        ByteBuffer byteBuffer = ByteBuffer.allocate(4*67);
        for(int i = 0; i < 67; i++) {
            floats[i] = (float) Math.random();
            //byteBuffer.putFloat((float) Math.random());
            byteBuffer.putFloat(floats[i]);
        }
        System.out.println(floats);


        Packet packet = PacketFactory.createPacket(3, byteBuffer.array());
        System.out.println(packet);
    }
}
