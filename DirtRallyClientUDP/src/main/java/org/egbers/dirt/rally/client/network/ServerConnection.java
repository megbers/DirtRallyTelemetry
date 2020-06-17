package org.egbers.dirt.rally.client.network;

import org.egbers.dirt.rally.client.model.PacketFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ServerConnection {

    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 20777;
    private DatagramSocket socket;

    public ServerConnection() {
        try {
            InetAddress address = InetAddress.getByName(HOSTNAME);
            socket = new DatagramSocket(PORT, address);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    //TODO Make multithreaded
    public void run() {
        try {
            while (true) {
                byte[] rawData = new byte[256];
                DatagramPacket response = new DatagramPacket(rawData, rawData.length);
                socket.receive(response);

                PacketFactory.createPacket(PacketFactory.TYPE_3, rawData);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}
