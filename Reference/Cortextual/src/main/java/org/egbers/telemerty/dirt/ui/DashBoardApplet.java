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

    private UISettings settings;
    public UISettings getSettings() {
        return settings;
    }

    private ControlP5 cp5;
    private Controller controller;



    int mx, my = 0;
    boolean isWindowBeingDragged = false;

    //settings
    int wheelRot = 400;
    int gearColorDuration = 250;
    boolean gballFlash = false;
    boolean colorGearChange, colorPedals, colorSpeedo, colorRevs,
            showMax, showAvg, showDist, colorSusp, colorGball, clutchIndicator;
    int maxSpeed = 260;
    int maxRevs = 11000;
    int mode = 1;
    String settingsIn[], udpSettingsin[];
    float uiScale;

    boolean debugging = false;
    boolean debuggingSettings = false;
    //boolean fullMode = false;
    //boolean compactMode = false;
    //boolean wheelMode = false;
    String ip = "127.0.0.1";
    float clutchD = 0;
    float clutchSens = 0.2f;
    boolean cKey = false;
    int winPosX = 0;
    int winPosY = 0;
    boolean borderless = false;
    boolean showGTrace = true;
    boolean alwaysTop = true;
    int traceLength = 60;

    int portRX = 20777;

    float maxRpm = 0;
    Point winLoc;
    Point winLoc2;


    UDP udpRX;
    boolean udpOpen = false;
    String pathPrefix = "C:\\Users\\matt_\\Development\\java\\DirtRallyTelemetry\\Reference\\Cortextual\\src\\main\\resources\\data\\";
    String settingsPath = pathPrefix + "settings";
    String UDPSettingsPath = pathPrefix + "connection.cfg";



    // All Data Below Here???
    PVector gballOld[] = new PVector[301];
    PVector gballNew;
    float timer;
    boolean gearChanged = false;
    boolean changedUp = true;
    float previousGear = 0;
    float currentGear = 0;
    boolean revsReset = false;
    String gearValue;
    PImage img, imgDrag, wheelImg, bLightOn, bLightOff, kPanel;

    float gForceX = 0.0f;
    float gForceY = 0.0f;
    float steeringOutput = 0.0f;

    float fdiff = 0.0f;
    float cdiff = 0.0f;
    float rdiff = 0.0f;
    float maxSpd = 0.0f;
    float avgSpd = 0.0f;



    public void settings() {
        //load settings
        settingsIn = loadStrings(settingsPath);
        settings = new UISettings(settingsIn, DARKGREY, WHITE, 0xffebf8ff, 0xff12acdb);

        gballFlash = Boolean.valueOf(settingsIn[0]);
        wheelRot = Integer.valueOf(settingsIn[1]);
        colorGearChange = Boolean.valueOf(settingsIn[2]);
        colorPedals = Boolean.valueOf(settingsIn[3]);
        colorSpeedo = Boolean.valueOf(settingsIn[4]);
        colorRevs = Boolean.valueOf(settingsIn[5]);
        showMax = Boolean.valueOf(settingsIn[6]);
        showAvg = Boolean.valueOf(settingsIn[7]);
        showDist = Boolean.valueOf(settingsIn[8]);
        colorSusp = Boolean.valueOf(settingsIn[9]);
        colorGball = Boolean.valueOf(settingsIn[10]);
        //useJoystick = Boolean.valueOf(settingsin[11]);
        maxRevs = Integer.valueOf(settingsIn[12]);
        maxSpeed = Integer.valueOf(settingsIn[13]);
        uiScale = Float.valueOf(settingsIn[14]);
        mode = Integer.valueOf(settingsIn[15]);
        cKey = Boolean.valueOf(settingsIn[16]);
        clutchIndicator = Boolean.valueOf(settingsIn[17]);
        winPosX = Integer.valueOf(settingsIn[18]);
        winPosY = Integer.valueOf(settingsIn[19]);
        borderless = Boolean.valueOf(settingsIn[20]);
        showGTrace = Boolean.valueOf(settingsIn[21]);
        traceLength = Integer.valueOf(settingsIn[22]);
        alwaysTop = Boolean.valueOf(settingsIn[23]);

        //load connection settings
        udpSettingsin = loadStrings(UDPSettingsPath);
        ip = udpSettingsin[1];
        portRX = Integer.valueOf(udpSettingsin[3]);



        //set window size
        int xSize = 1100;
        int ySize = 180;
        xSize = PApplet.parseInt(xSize * (uiScale / 100));
        ySize = PApplet.parseInt(ySize * (uiScale / 100));

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
        surface.setResizable(borderless);
        PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
        PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas) awtSurface.getNative();
        smoothCanvas.getFrame().setAlwaysOnTop(alwaysTop);
        smoothCanvas.getFrame().removeNotify();
        smoothCanvas.getFrame().setUndecorated(borderless);
        //smoothCanvas.getFrame().setOpacity(1.0);
        //smoothCanvas.getFrame().setBackground(new Color(0, 0, 0, 126));
        smoothCanvas.getFrame().setLocation(winPosX - (width / 2), winPosY);
        smoothCanvas.getFrame().addNotify();

        if (debuggingSettings) {
            println("Settings Loaded from file: ");
            println("G-Ball Flash: " + gballFlash + " Wheel Rotation: " + wheelRot + " Color Gear Change: " + colorGearChange);
            println("Color Pedals: " + colorPedals + " Color Speedo: " + colorSpeedo + " Color Revs: " + colorRevs);
            println("Show Max: " + showMax + " Show Average: " + showAvg + " Show Distance: " + showDist);
            println("Color Suspension: " + colorSusp + " Color G-Ball: " + colorGball);
            println(" Max Revs: " + maxRevs + " Max Speed: " + maxSpeed + " UI Scale: " + uiScale + "%");
        }
        //initialise gball values
        for (int i = 0; i < traceLength + 1; i++) {
            gballOld[i] = new PVector(96, 75);
        }

        //gballOld = new PVector(96, 75);
        gballNew = new PVector(96, 75);

        smooth();
        background(0);

        if (cKey) {
            img = loadImage(pathPrefix + "bgy.png");
        } else {
            img = loadImage(pathPrefix + "bg.png");
        }
        //imgDrag=loadImage(pathPrefix + "bgw.png");
        kPanel = loadImage(pathPrefix + "kPanel.png");
        bLightOn = loadImage(pathPrefix + "bLightOn.png");
        bLightOff = loadImage(pathPrefix + "bLightOff.png");
        if (!settings.getCompactMode()) {
            wheelImg = loadImage(pathPrefix + "wheel.png");
        } else {
            wheelImg = loadImage(pathPrefix + "wheelCompact.png");
        }
        //draw controllers on screen
        controller = ControllerFactory.createController(mode);
        cp5 = controller.drawControls(this);



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

        PFont p1 = createFont("arial", 24, true);

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
        if (((urpm) > (maxRpm * .95f)) && colorRevs) {
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
        if (colorSpeedo)
            cp5.getController("speed").setColorForeground(color(255, 255 - map(uspeed, 0, maxSpeed, 0, 255), 255 - map(uspeed, 0, maxSpeed, 0, 255)));
        cp5.getController("suspFL").setValue(ususpFL);
        float suspFlashPct = .9f;
        if ((ususpFL > (cp5.getController("suspFL").getMax() * suspFlashPct)) && colorSusp) {
            cp5.getController("suspFL").setColorForeground(RED);
        } else {
            cp5.getController("suspFL").setColorForeground(WHITE);
        }
        cp5.getController("suspFR").setValue(ususpFR);
        if ((ususpFR > (cp5.getController("suspFR").getMax() * suspFlashPct)) && colorSusp) {
            cp5.getController("suspFR").setColorForeground(RED);
        } else {
            cp5.getController("suspFR").setColorForeground(WHITE);
        }
        cp5.getController("suspAL").setValue(ususpAL);
        if ((ususpAL > (cp5.getController("suspAL").getMax() * suspFlashPct)) && colorSusp) {
            cp5.getController("suspAL").setColorForeground(RED);
        } else {
            cp5.getController("suspAL").setColorForeground(WHITE);
        }
        cp5.getController("suspAR").setValue(ususpAR);
        if ((ususpFL > (cp5.getController("suspAR").getMax() * suspFlashPct)) && colorSusp) {
            cp5.getController("suspAR").setColorForeground(RED);
        } else {
            cp5.getController("suspAR").setColorForeground(WHITE);
        }

        //GForce Processing
        gForceX = constrain(ugForce_X * 10, -20, 20);
        gForceY = constrain(ugForce_Y * 10, -20, 20);


        //Steering Processing
        steeringOutput = radians(usteering * (wheelRot / 2));


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
        scale(uiScale / 100);
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
                colorGear();
                drawWheel();
            }

        }

        strokeWeight(1);



        if (settings.getFullMode()) {

            //Draw GBall
            if (!isWindowBeingDragged) {
                ellipseMode(CENTER);
                stroke(0);

                //if gballFlash is turned on
                //Flash G-Ball enclosure red if gforce > 15
                if (((abs(gForceX) > 15) || (abs(gForceX) > 15)) && (gballFlash)) {
                    fill(RED);
                    // red outer ellipse for G-Ball
                    //with grey central ellipse
                    ellipse(96, 75, 120, 120);
                    fill(DARKGREY);
                    ellipse(96, 75, 70, 70);
                } else if (((abs(gForceX) > 10) || (abs(gForceX) > 10)) && (gballFlash)) {
                    fill(ORANGE);
                    // orange outer ellipse for G-Ball
                    //with grey central ellipse
                    ellipse(96, 75, 120, 120);
                    fill(DARKGREY);
                    ellipse(96, 75, 70, 70);
                } else {
                    fill(DARKGREY);
                    //background ellipse for G-Ball
                    ellipse(96, 75, 120, 120);
                }
                //Black lines on G-Ball
                fill(DARKGREY);
                ellipse(96, 75, 70, 70);
                //stroke(255);
                fill(255, 0);
                ellipse(96, 75, 70, 70);
                line(96, 15, 96, 135);
                line(36, 75, 156, 75);
                fill(255, 0);

                //Draw Trace of previous 10 GBall positions
                fill(WHITE);
                noStroke();
                stroke(255, 255, 255, 200);
                noFill();
                if (showGTrace) {
                    //curveTightness(.5);
                    strokeWeight(2);
                    beginShape();
                    curveVertex(gballOld[0].x, gballOld[0].y);
                    for (int i = 1; i < traceLength - 1; i++) {

                        //ellipse(gballOld[i].x, gballOld[i].y, 5, 5);
                        //line(gballOld[i-1].x, gballOld[i-1].y,gballOld[i].x, gballOld[i].y);
                        if (dist(gballOld[i].x, gballOld[i].y, gballOld[i - 1].x, gballOld[i - 1].y) > 2) {
                            curveVertex(gballOld[i].x, gballOld[i].y);
                        }

                        //moving avg
                        //float avgX = (gballOld[i].x + gballOld[i+1].x + gballOld[i+2].x) / 3;
                        //float avgY = (gballOld[i].y + gballOld[i+1].y + gballOld[i+2].y) /3;
                        //curveVertex(avgX, avgY);
                    }
                    curveVertex(gballOld[traceLength].x, gballOld[traceLength].y);
                    endShape();
                }
                //Draw G-Ball
                gballNew.x = map(gForceX, -20, 20, -40, 40) + 96;
                gballNew.y = map(gForceY, -20, 20, -40, 40) + 75;
                //if the G-Ball is going to jump more than 5 pixels in one frame
                //don't let it
                //if (dist(gballNew.x, gballNew.y, gballOld.x, gballOld.y) > 25) {
                //  gballNew.x = gballOld.x;
                //  gballNew.y = gballOld.y;
                //}
                //shading for G-Ball
                //color shaded or white depending on settings

                if (colorGball) {
                    //shadow
                    //fill(0, 0, 0, 100);
                    //noStroke();
                    //ellipse(gballNew.x, gballNew.y, 20, 20);
                    for (int i = 1; i < 17; i++) {
                        noFill();
                        stroke(color(180, 180 - (180 / 16) * i, 180 - (180 / 16) * i));
                        ellipse(gballNew.x, gballNew.y, i, i);
                    }
                } else {
                    fill(WHITE);
                    stroke(0);
                    ellipse(gballNew.x, gballNew.y, 16, 16);
                }

                //Differential draw
                rectMode(CENTER);
                stroke(0);
                fill(255);
                rect(1000 + fdiff, 40, 10, 30);
                rect(1000, 80 + cdiff, 30, 10);
                rect(1000 + rdiff, 120, 10, 30);
            }
            if (showGTrace) {
                //Update GBall previous positions
                for (int i = traceLength - 1; i > -1; i--) {
                    gballOld[i + 1].x = gballOld[i].x;
                    gballOld[i + 1].y = gballOld[i].y;
                }
                gballOld[0].x = gballNew.x;
                gballOld[0].y = gballNew.y;
            }


        } else if (settings.getCompactMode() && !isWindowBeingDragged) {
            //Black panels for keying
            fill(BLACK);
            stroke(0);
            //ellipse(275,75,85,85);
            image(kPanel, 0, 136);
            //rect(0,136,550,38);
            //Clutch Lights
            if (!settings.getFullMode() & clutchD > clutchSens & clutchIndicator) {
                image(bLightOn, 175, 143, 25, 25);
                image(bLightOn, 350, 143, 25, 25);
            } else if (clutchIndicator) {
                image(bLightOff, 175, 143, 25, 25);
                image(bLightOff, 350, 143, 25, 25);
            }
        } else {
            if (!settings.getFullMode() & clutchD > clutchSens & clutchIndicator) {
                fill(0, 0);
                stroke(color(0, 0, 255));
                rect(24, 13, 26, 121);
                rect(23, 12, 28, 123);
                rect(22, 11, 30, 125);
            }
        }
        //if (debugging) println("Draw complete.");
    }


    public void colorGear() {
        if (currentGear == 10 || currentGear < 0) {
            //make reverse lower than the other gears for the shifting-up logic
            currentGear = -10;
            gearValue = "R";
        } else if (currentGear == 0) {
            gearValue = "N";
        } else {
            gearValue = String.valueOf(currentGear);
            gearValue = gearValue.substring(0, 1);
        }


        //check direction of gear change and time stamp it
        if (previousGear > currentGear) {
            timer = millis();
            changedUp = false;
        } else if (previousGear < currentGear) {
            timer = millis();
            changedUp = true;
        }

        //Checks that gear change was recent
        if ((timer > 0) && (millis() - timer < gearColorDuration)) {
            gearChanged = true;
        } else {
            gearChanged = false;
        }

        // if you're in neutral or reverse the gear indicator is red
        // if you're shifting up from any gear other than neutral it's green, down it's red
        // neutral has to be ignored because H-Shifters go to it between gears and
        // everything becomes an upshift from neutral

        if (colorGearChange) {
            if ((gearValue == "R") || (gearValue == "N")) {
                cp5.getController("gearLabel").setColorValue(RED);
            } else if ((changedUp) && (gearChanged)) {
                cp5.getController("gearLabel").setColorValue(GREEN);
            } else if ((!changedUp) && (gearChanged)) {
                cp5.getController("gearLabel").setColorValue(RED);
            } else {
                cp5.getController("gearLabel").setColorValue(WHITE);
            }
        }

        if (currentGear != 0) previousGear = currentGear;
        if (debugging) println("ColorGear complete.");
    }

    public void drawWheel() {

        pushMatrix();
        if (settings.getFullMode()) {
            translate(684, 75);
            rotate(steeringOutput);
            image(wheelImg, -50, -50, 100, 100);
        } else if (settings.getCompactMode()) {
            translate(275, 75);
            rotate(steeringOutput);
            image(wheelImg, -60, -60, 120, 120);
        } else {
            translate(125, 75);
            rotate(steeringOutput);
            image(wheelImg, -60, -60, 120, 120);
        }

        popMatrix();
        //if (debugging) println("DrawWheel complete.");
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

    public void closeUDP() {
        udpRX.dispose();
        udpOpen = false;
        if (debugging) println("UDP Closed");
    }

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
        if ((mouseButton == LEFT) && borderless) {
            isWindowBeingDragged = false;
            PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
            PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas) awtSurface.getNative();
            winLoc2 = smoothCanvas.getFrame().getLocation();
            mx = mouseX;
            my = mouseY;
        }
    }

    public void mouseReleased() {
        if ((mouseButton == LEFT) && borderless) {
            isWindowBeingDragged = false;
            int newPosX = (mouseX - mx);
            int newPosY = (mouseY - my);
            surface.setLocation(winLoc2.x + newPosX, winLoc2.y + newPosY);
        }
    }

}
