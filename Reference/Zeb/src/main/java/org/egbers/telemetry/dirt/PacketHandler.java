package org.egbers.telemetry.dirt;

public class PacketHandler {

    private DashboardApplet applet;

    public PacketHandler(DashboardApplet applet) {
        this.applet = applet;

    }

    public void receive(byte[] data, String ip, int portRX){
        // Function to output all the game data received
        applet.fullOutput(data);

        // Time elapsed since game start
        //
        //Bitwise & ([pos] & 0xff), Takes byte and multiplies it by 00000000 00000000 11111111, so that the only thing remaining are the last 8 bits.
        //Float.intBitsToFloat converts binary to a float integer and makes it equal to the stated vars.
        int pos = 0;
        float tTime = parseData(data, pos);

        // Lap time
        pos = 4;
        float lapTime = parseData(data, pos);

        // Distance
        pos = 8;
        float distance = parseData(data, pos);

        //Pos X
        pos = 16;
        float posx = parseData(data, pos);

        //Pos Y
        pos = 20;
        float posy = parseData(data, pos);

        // Speed, *3.6 for Km/h
        pos = 28;
        float speed = parseData(data, pos) * 3.6f;

        //Suspension travel aft left
        pos = 68;
        float suspAL = parseData(data, pos);
        //Suspension travel aft right
        pos = 72;
        float suspAR = parseData(data, pos);
        //Suspension travel fwd left
        pos = 76;
        float suspFL = parseData(data, pos);
        //Suspension travel fwd right
        pos = 80;
        float suspFR = parseData(data, pos);

        //Wheel speed aft left
        pos = 100;
        float wspAL = parseData(data, pos);
        //Wheel speed aft right
        pos = 104;
        float wspAR = parseData(data, pos);
        //Wheel speed fwd left
        pos = 108;
        float wspFL = parseData(data, pos);
        //Wheel speed fwd right
        pos = 112;
        float wspFR = parseData(data, pos);

        //Throttle 01
        pos = 116;
        float throttle = parseData(data, pos);

        //steering
        pos = 120;
        float steering = parseData(data, pos);

        //Brakes 0-1
        pos = 124;
        float brakes = parseData(data, pos);

        //Clutch 0-1
        pos = 128;
        float clutch = parseData(data, pos);

        // Gear, neutral = 0
        pos = 132;
        float gear = parseData(data, pos);

        // gForceX
        pos = 136;
        float gForce_X = parseData(data, pos);

        // gForceY
        pos = 140;
        float gForce_Y = parseData(data, pos);

        // Current lap, starts at 0
        pos = 144;
        float cLap = parseData(data, pos);

        // RPM, requires *10 for realistic values
        pos = 148;
        float rpm = parseData(data, pos) * 10;

        // Debug the received values
        applet.gameDataOutput(tTime, lapTime, speed, gear, cLap, rpm, gForce_X);

        // Output the values to the dashboard
        applet.setControllerValue("lapTime", lapTime);
        applet.setControllerValue("distance", distance);
        applet.setControllerValue("posx", posx);
        applet.setControllerValue("posy", posy);
        applet.setControllerValue("brakes", brakes);
        //applet.setControllerValue("steering", steering);
        applet.setControllerValue("clutch", clutch);
        applet.setControllerValue("throttle", throttle);
        applet.setControllerValue("rpm", rpm);
        applet.setControllerValue("gear", gear);
        applet.setControllerValue("speed", speed);
        applet.setControllerValue("suspFL", suspFL);
        applet.setControllerValue("suspFR", suspFR);
        applet.setControllerValue("suspAL", suspAL);
        applet.setControllerValue("suspAR", suspAR);

        //GForce Processing
        applet.setGForceX(gForce_X);
        applet.setGForceY(gForce_Y);

        //Steering Processing
        applet.setSteeringOutput(steering);

        //Differential math
        applet.setFdiff(wspFL, wspFR);
        applet.setRdiff(wspAL, wspAR);
        applet.setCdiff(wspFL, wspFR, wspAL, wspAR);

        //Stats
        applet.setMaxSpeed(speed);

        //Avg Spd
        float avgSpd = distance/lapTime;
        applet.setControllerValue("avgSpd", avgSpd);

    }

    private float parseData(byte[] data, int pos) {
        return Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
    }

}
