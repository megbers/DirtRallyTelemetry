package org.egbers.telemerty.dirt.ui;

import controlP5.ControlP5;
import hypermedia.net.UDP;
import org.egbers.telemerty.dirt.network.Packet;
import org.egbers.telemerty.dirt.network.PacketHandler;
import org.egbers.telemerty.dirt.ui.controller.Controller;
import org.egbers.telemerty.dirt.ui.controller.ControllerFactory;
import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;

public class DashBoardApplet extends PApplet {
    public final int RED = color(255, 0, 0);
    public final int GREEN = color(0, 255, 0);
    public final int BLUE = color(0, 0, 255);
    public final int WHITE = color(255, 255, 255);
    public final int BLACK = color(0, 0, 0);
    public final int DARKGREY = color(45, 45, 45);
    public final int ORANGE = color(255, 128, 0);

    public String pathPrefix = "C:\\Users\\matt_\\Development\\java\\DirtRallyTelemetry\\Reference\\Cortextual\\src\\main\\resources\\data\\";
    String settingsPath = pathPrefix + "settings";
    String UDPSettingsPath = pathPrefix + "connection.cfg";

    String settingsIn[], udpSettingsin[];
    private UISettings settings;
    public UISettings getSettings() {
        return settings;
    }


    private ControlP5 cp5;
    private Controller controller;


    boolean debugging = false;
    boolean debuggingSettings = false;


    String ip = "127.0.0.1";
    int portRX = 20777;
    UDP udpRX;
    boolean udpOpen = false;


    Point winLoc;
    Point winLoc2;
    int mx, my = 0;



    // All Data Below Here???
    public float clutchD = 0;
    public PImage img, imgDrag;
    public boolean isWindowBeingDragged = false; //State variable
    public float currentGear = 0; //State variable
    public float gForceX = 0.0f;
    public float gForceY = 0.0f;
    public float fdiff = 0.0f;
    public float cdiff = 0.0f;
    public float rdiff = 0.0f;
    public float steeringOutput = 0.0f;

    public PVector gballOld[] = new PVector[301];
    public PVector gballNew;

    //For Drawing Gears
    public String gearValue;
    public float previousGear = 0;
    public float timer;
    public boolean gearChanged = false;
    public boolean changedUp = true;


    boolean revsReset = false;
    float maxSpd = 0.0f;
    float avgSpd = 0.0f;
    float maxRpm = 0;


    public void settings() {
        //load settings
        settingsIn = loadStrings(settingsPath);
        settings = new UISettings(settingsIn, DARKGREY, WHITE, 0xffebf8ff, 0xff12acdb);



        //load connection settings
        udpSettingsin = loadStrings(UDPSettingsPath);
        ip = udpSettingsin[1];
        portRX = Integer.valueOf(udpSettingsin[3]);



        //set window size
        int xSize = 1100;
        int ySize = 180;
        xSize = PApplet.parseInt(xSize * (settings.getUiScale() / 100));
        ySize = PApplet.parseInt(ySize * (settings.getUiScale() / 100));

        if (settings.getCompactMode()) {
            xSize = xSize / 2;
        } else if (settings.getWheelMode()) {
            xSize = PApplet.parseInt(xSize / 4.4f);
            ySize = PApplet.parseInt(ySize / 1.2f);
        }
        size(xSize, ySize);
    }


//            _
//           | |
//   ___  ___| |_ _   _ _ __
//  / __|/ _ \ __| | | | '_ \
//  \__ \  __/ |_| |_| | |_) |
//  |___/\___|\__|\__,_| .__/
//                     | |
//                     |_|
    public void setup() {
        PImage titlebaricon = loadImage(pathPrefix + "Icon.png");
        surface.setIcon(titlebaricon);
        surface.setTitle("DiRT Telemetry Tool");
        surface.setResizable(settings.getBorderless());
        PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
        PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas) awtSurface.getNative();
        smoothCanvas.getFrame().setAlwaysOnTop(settings.getAlwaysTop());
        smoothCanvas.getFrame().removeNotify();
        smoothCanvas.getFrame().setUndecorated(settings.getBorderless());
        //smoothCanvas.getFrame().setOpacity(1.0);
        //smoothCanvas.getFrame().setBackground(new Color(0, 0, 0, 126));
        smoothCanvas.getFrame().setLocation(settings.getWinPosX() - (width / 2), settings.getWinPosY());
        smoothCanvas.getFrame().addNotify();

        if (debuggingSettings) {
            println(settings.toString());
        }
        //initialise gball values
        for (int i = 0; i < settings.getTraceLength() + 1; i++) {
            gballOld[i] = new PVector(96, 75);
        }

        //gballOld = new PVector(96, 75);
        gballNew = new PVector(96, 75);

        smooth();
        background(0);

        if (settings.getcKey()) {
            img = loadImage(pathPrefix + "bgy.png");
        } else {
            img = loadImage(pathPrefix + "bg.png");
        }

        //draw controllers on screen
        controller = ControllerFactory.createController(this, settings.getMode());
        cp5 = controller.drawControls();



        createUDP();

        if (debugging) println("Setup complete.");
    }




    public void updateControllers(Packet packet) {
        updateControllers(packet.getUtTime(), packet.getUlapTime(), packet.getUdistance(), packet.getUposx(), packet.getUposy(), packet.getUspeed(), packet.getUsuspAL(), packet.getUsuspAR(),
                packet.getUsuspFL(), packet.getUsuspFR(), packet.getUwspAL(), packet.getUwspAR(), packet.getUwspFL(), packet.getUwspFR(), packet.getUthrottle(), packet.getUbrakes(), packet.getUclutch(),
                packet.getUsteering(), packet.getUgear(), packet.getUgForce_X(), packet.getUgForce_Y(), packet.getUcLap(), packet.getUrpm());
    }

    private void updateControllers(float utTime, float ulapTime, float udistance, float uposx, float uposy, float uspeed, float ususpAL, float ususpAR, float ususpFL,
                                  float ususpFR, float uwspAL, float uwspAR, float uwspFL, float uwspFR, float uthrottle, float ubrakes, float uclutch, float usteering,
                                  float ugear, float ugForce_X, float ugForce_Y, float ucLap, float urpm) {

        float distKm = udistance / 1000;
        float timeHrs = ulapTime / 3600;

        //Record current gear for comparison to previous
        currentGear = ugear;

        // Reset Rev Limit if it's a new run to allow for different cars
        if (ulapTime == 0 && (!revsReset)) {
            newRun();
            revsReset = true;
        } else if (ulapTime > 0 && revsReset) {
            revsReset = false;
        }

        if (urpm > maxRpm) {
            maxRpm = urpm;
        }
        // If revs are over 95% Change rev bar to red
        if (((urpm) > (maxRpm * .95f)) && settings.getColorRevs()) {
            cp5.getController("rpm").setColorForeground(RED);
        } else {
            cp5.getController("rpm").setColorForeground(WHITE);
        }

        String time = timeConversion(PApplet.parseInt(ulapTime * 100));

        // Output the values to the dashboard
        cp5.getController("rpm").setValue(urpm);

        //If the run just ended, the lapTime will reset to 0, but the distance wont reset,
        //in this case we don't want to refresh the laptime until a new run starts,
        //i.e. The distance is also reset
        if (!(udistance > 0 & ulapTime == 0)) {
            cp5.getController("lapTime").setStringValue(time);
        }
        cp5.getController("distance").setStringValue(nf(udistance, 5, 1));

        cp5.getController("posx").setValue(uposx);
        cp5.getController("posy").setValue(uposy);
        cp5.getController("brakes").setValue(ubrakes);
        cp5.getController("clutch").setValue(uclutch);
        cp5.getController("throttle").setValue(uthrottle);
        cp5.getController("gearLabel").setStringValue(gearValue);
        cp5.getController("speed").setValue(uspeed);
        if (settings.getColorSpeedo())
            cp5.getController("speed").setColorForeground(color(255, 255 - map(uspeed, 0, settings.getMaxSpeed(), 0, 255), 255 - map(uspeed, 0, settings.getMaxSpeed(), 0, 255)));
        cp5.getController("suspFL").setValue(ususpFL);
        float suspFlashPct = .9f;
        if ((ususpFL > (cp5.getController("suspFL").getMax() * suspFlashPct)) && settings.getColorSusp()) {
            cp5.getController("suspFL").setColorForeground(RED);
        } else {
            cp5.getController("suspFL").setColorForeground(WHITE);
        }
        cp5.getController("suspFR").setValue(ususpFR);
        if ((ususpFR > (cp5.getController("suspFR").getMax() * suspFlashPct)) && settings.getColorSusp()) {
            cp5.getController("suspFR").setColorForeground(RED);
        } else {
            cp5.getController("suspFR").setColorForeground(WHITE);
        }
        cp5.getController("suspAL").setValue(ususpAL);
        if ((ususpAL > (cp5.getController("suspAL").getMax() * suspFlashPct)) && settings.getColorSusp()) {
            cp5.getController("suspAL").setColorForeground(RED);
        } else {
            cp5.getController("suspAL").setColorForeground(WHITE);
        }
        cp5.getController("suspAR").setValue(ususpAR);
        if ((ususpFL > (cp5.getController("suspAR").getMax() * suspFlashPct)) && settings.getColorSusp()) {
            cp5.getController("suspAR").setColorForeground(RED);
        } else {
            cp5.getController("suspAR").setColorForeground(WHITE);
        }

        //GForce Processing
        gForceX = constrain(ugForce_X * 10, -20, 20);
        gForceY = constrain(ugForce_Y * 10, -20, 20);


        //Steering Processing
        steeringOutput = radians(usteering * (settings.getWheelRot() / 2));


        //Differential math
        fdiff = (uwspFL * -1) + uwspFR;
        rdiff = (uwspAL * -1) + uwspAR;
        cdiff = ((uwspFL + uwspFR) * -1) + (uwspAL + uwspAR);

        //Stats
        //Max Speed
        if (uspeed > maxSpd) {
            maxSpd = uspeed;
        }

        //Prevent max speed from going up while rolling to the start line
        if (udistance <= 0) {
            maxSpd = 0;
        }
        cp5.getController("maxSpd").setStringValue(nf(maxSpd, 3, 1));

        //  Avg Spd
        //Reset average speed only at the start of a new run
        if (udistance <= 0 & ulapTime == 0) {
            avgSpd = 0;
        } else if (ulapTime != 0) {
            avgSpd = distKm / timeHrs;
        }

        cp5.getController("avgSpd").setStringValue(nf(avgSpd, 3, 1));

        clutchD = uclutch;


        if (debugging) println("UpdateControllers complete.");
    }

    private String timeConversion(int centiseconds) {

        final int SECONDS_IN_A_MINUTE = 60;
        final int CENTISECONDS_IN_A_SECOND = 100;

        int seconds = centiseconds / CENTISECONDS_IN_A_SECOND;
        centiseconds -= seconds * CENTISECONDS_IN_A_SECOND;

        int minutes = seconds / SECONDS_IN_A_MINUTE;
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        return nf(minutes, 2) + ":" + nf(seconds, 2) + ":" + nf(centiseconds, 2);
    }

    public void gameDataOutput(float tTime, float lapTime, float speed, float gearNum, float cLap, float rpm, float wspFL, float steering) {
        println("Total time: " + tTime);
        println("Lap time: " + lapTime);
        println("Speed: " + speed);
        println("Gear: " + gearNum);
        println("Current lap: " + cLap);
        println("RPM: " + rpm);
        println("SuspFL: " + wspFL);
        //println("Steering: " + steeringOutput);
    }

    // Function that outputs all the received game data
    public void fullOutput(byte[] data) {

        // Loop all the received bytes
        for (int i = 0; i <= data.length - 1; i++) {

            // Values consist of 4 bytes
            if (i % 4 == 0) {

                // Combine 4 bytes to the value
                float val = Float.intBitsToFloat((data[i] & 0xff) | ((data[i + 1] & 0xff) << 8) | ((data[i + 2] & 0xff) << 16) | ((data[i + 3] & 0xff) << 24));

                // Output the 'raw' value
                println("Value received at position " + i + " = " + val);

            }
        }
    }

    public void setFieldColor(String field, int color) {
        cp5.getController(field).setColorValue(color);
    }

//       _
//      | |
//    __| |_ __ __ ___      __
//   / _` | '__/ _` \ \ /\ / /
//  | (_| | | | (_| |\ V  V /
//   \__,_|_|  \__,_| \_/\_/

    public void draw() {
        //Prevent crash if app is run for 828 days lol
        if (frameCount == -1) frameCount = 1;

        //set UI Scale
        scale(settings.getUiScale() / 100);
        if (udpOpen) {
            //PVector gballMove = new PVector(0,0);
            //background(0);
            image(img, 0, 0);
            //handle window dragging
            if (isWindowBeingDragged) {
                image(imgDrag, 0, 0);
                cp5.setVisible(false);
            } else {
                cp5.setVisible(true);
                controller.colorGear();
                controller.drawWheel();
            }

        }

        strokeWeight(1);
        controller.draw();

    }


    public void createUDP() {
        // Create new object for receiving
        PacketHandler packetHandler = new PacketHandler(this);
        udpRX = new UDP(packetHandler, portRX, ip);
        udpRX.log(false);
        udpRX.listen(true);
        udpOpen = true;
        if (debugging) println("UDP Opened");
    }

//    public void closeUDP() {
//        udpRX.dispose();
//        udpOpen = false;
//        if (debugging) println("UDP Closed");
//    }

/*void resetAll() {
 closeUDP();
 createUDP();
 cp5.dispose();
 drawControllers();
}
*/



    public void newRun() {
        maxRpm = 0;
        maxSpd = 0;
    }

    public void exit() {
        //Exit handler
        //save window location
        PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
        PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas) awtSurface.getNative();
        winLoc = smoothCanvas.getFrame().getLocation();
        settingsIn[18] = String.valueOf(winLoc.x + (width / 2));
        settingsIn[19] = String.valueOf(winLoc.y);
        saveStrings(settingsPath, settingsIn);
        super.exit();
    }

    public void mousePressed() {
        if ((mouseButton == LEFT) && settings.getBorderless()) {
            isWindowBeingDragged = false;
            PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
            PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas) awtSurface.getNative();
            winLoc2 = smoothCanvas.getFrame().getLocation();
            mx = mouseX;
            my = mouseY;
        }
    }

    public void mouseReleased() {
        if ((mouseButton == LEFT) && settings.getBorderless()) {
            isWindowBeingDragged = false;
            int newPosX = (mouseX - mx);
            int newPosY = (mouseY - my);
            surface.setLocation(winLoc2.x + newPosX, winLoc2.y + newPosY);
        }
    }

}
