package org.egbers.telemerty.dirt.network;

import org.egbers.telemerty.dirt.ui.DashBoardApplet;

public class PacketHandler {
    private DashBoardApplet applet;
    private boolean debugging = false;

    public PacketHandler(DashBoardApplet applet) {
        this.applet = applet;
    }

    public void receive(byte[] data, String ip, int portRX) {
        if (debugging) applet.fullOutput(data);

        // Function to output all the game data received

        //Bitwise & ([pos] & 0xff), Takes byte and multiplies it by 00000000 00000000 11111111, so that the only thing remaining are the last 8 bits.
        //Float.intBitsToFloat converts binary to a float integer and makes it equal to the stated vars.
        int pos = 0;
        float tTime = parseFieldValue(data, pos);
        // Lap time
        pos = 4;
        float lapTime = parseFieldValue(data, pos);
        // Distance
        pos = 8;
        float distance = parseFieldValue(data, pos);
        if (distance < 0) distance = 0;
        //Pos X
        pos = 16;
        float posx = parseFieldValue(data, pos);
        //Pos Y
        pos = 20;
        float posy = parseFieldValue(data, pos);
        // Speed, *3.6 for Km/h
        pos = 28;
        float speed = parseFieldValue(data, pos) * 3.6f;
        //Suspension travel aft left
        pos = 68;
        float suspAL = parseFieldValue(data, pos);
        //Suspension travel aft right
        pos = 72;
        float suspAR = parseFieldValue(data, pos);
        //Suspension travel fwd left
        pos = 76;
        float suspFL = parseFieldValue(data, pos);
        //Suspension travel fwd right
        pos = 80;
        float suspFR = parseFieldValue(data, pos);
        //Wheel speed aft left
        pos = 100;
        float wspAL = parseFieldValue(data, pos);
        //Wheel speed aft right
        pos = 104;
        float wspAR = parseFieldValue(data, pos);
        //Wheel speed fwd left
        pos = 108;
        float wspFL = parseFieldValue(data, pos);
        //Wheel speed fwd right
        pos = 112;
        float wspFR = parseFieldValue(data, pos);
        //Throttle 0-1
        pos = 116;
        float throttle = parseFieldValue(data, pos);
        //Brakes 0-1
        pos = 124;
        float brakes = parseFieldValue(data, pos);
        //Clutch 0-1
        pos = 128;
        float clutch = parseFieldValue(data, pos);
        //steering
        pos = 120;
        float steering = parseFieldValue(data, pos);
        // Gear, neutral = 0
        pos = 132;
        float gear = parseFieldValue(data, pos);
        // gForceX
        pos = 136;
        float gForce_X = parseFieldValue(data, pos);
        // gForceY
        pos = 140;
        float gForce_Y = parseFieldValue(data, pos);
        // Current lap, starts at 0
        pos = 144;
        float cLap = parseFieldValue(data, pos);
        // RPM, requires *10 for realistic values
        pos = 148;
        float rpm = parseFieldValue(data, pos) * 10;
        // Debug the received values

        if (debugging) {
            applet.gameDataOutput(tTime, lapTime, speed, gear, cLap, rpm, gForce_X, steering);
        }

        Packet packet = new Packet(tTime, lapTime, distance, posx, posy, speed, suspAL,
                suspAR, suspFL, suspFR, wspAL, wspAR, wspFL, wspFR,
                throttle, brakes, clutch, steering, gear, gForce_X,
                gForce_Y, cLap, rpm);

        applet.updateControllers(packet);

    }

    private float parseFieldValue(byte[] data, int pos) {
        return Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
    }
}
