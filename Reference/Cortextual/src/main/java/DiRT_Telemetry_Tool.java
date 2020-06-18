import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import controlP5.*;
import hypermedia.net.*;
import processing.serial.*;
import processing.awt.PSurfaceAWT;

import java.awt.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class DiRT_Telemetry_Tool extends PApplet {

    final int RED = color(255, 0, 0);
    final int GREEN = color(0, 255, 0);
    final int BLUE = color(0, 0, 255);
    final int WHITE = color(255, 255, 255);
    final int BLACK = color(0, 0, 0);
    final int DARKGREY = color(45, 45, 45);
    final int ORANGE = color(255, 128, 0);

    int mx, my = 0;
    boolean isWindowBeingDragged = false;
    int frameDelay;
    //settings
    int wheelRot = 400;
    int bgColor = DARKGREY;
    int fgColor = WHITE;
    int lblColor = 0xffebf8ff;
    int activeColor = 0xff12acdb;
    int gearColorDuration = 250;
    boolean showGear = true;
    boolean alternateGear = false;
    boolean gballFlash = false;
    boolean colorGearChange, colorPedals, colorSpeedo, colorRevs,
            showMax, showAvg, showDist, colorSusp, colorGball, clutchIndicator;
    int maxSpeed = 260;
    int maxRevs = 11000;
    int mode = 1;
    String settingsin[], udpSettingsin[];
    float uiScale;
    int xSize = 1100;
    int ySize = 180;
    boolean debugging = false;
    boolean debuggingSettings = false;
    boolean fullMode = false;
    boolean compactMode = false;
    boolean wheelMode = false;
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
    boolean drawingControllers;
    Point winLoc;
    Point winLoc2;

    Serial port;
    ControlP5 cp5;
    UDP udpRX;
    boolean udpOpen = false;
    String pathPrefix = "C:\\Users\\matt_\\Development\\java\\DirtRallyTelemetry\\Reference\\Cortextual\\src\\main\\resources\\data\\";
    String settingsPath = pathPrefix + "settings";
    String UDPSettingsPath = pathPrefix + "connection.cfg";


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
    int pos;

    float fdiff = 0.0f;
    float cdiff = 0.0f;
    float rdiff = 0.0f;
    float maxSpd = 0.0f;
    float avgSpd = 0.0f;

    public void settings() {
        //load settings
        settingsin = loadStrings(settingsPath);
        gballFlash = Boolean.valueOf(settingsin[0]);
        wheelRot = Integer.valueOf(settingsin[1]);
        colorGearChange = Boolean.valueOf(settingsin[2]);
        colorPedals = Boolean.valueOf(settingsin[3]);
        colorSpeedo = Boolean.valueOf(settingsin[4]);
        colorRevs = Boolean.valueOf(settingsin[5]);
        showMax = Boolean.valueOf(settingsin[6]);
        showAvg = Boolean.valueOf(settingsin[7]);
        showDist = Boolean.valueOf(settingsin[8]);
        colorSusp = Boolean.valueOf(settingsin[9]);
        colorGball = Boolean.valueOf(settingsin[10]);
        //useJoystick = Boolean.valueOf(settingsin[11]);
        maxRevs = Integer.valueOf(settingsin[12]);
        maxSpeed = Integer.valueOf(settingsin[13]);
        uiScale = Float.valueOf(settingsin[14]);
        mode = Integer.valueOf(settingsin[15]);
        cKey = Boolean.valueOf(settingsin[16]);
        clutchIndicator = Boolean.valueOf(settingsin[17]);
        winPosX = Integer.valueOf(settingsin[18]);
        winPosY = Integer.valueOf(settingsin[19]);
        borderless = Boolean.valueOf(settingsin[20]);
        showGTrace = Boolean.valueOf(settingsin[21]);
        traceLength = Integer.valueOf(settingsin[22]);
        alwaysTop = Boolean.valueOf(settingsin[23]);

        //load connection settings
        udpSettingsin = loadStrings(UDPSettingsPath);
        ip = udpSettingsin[1];
        portRX = Integer.valueOf(udpSettingsin[3]);

        //set window size
        xSize = PApplet.parseInt(xSize * (uiScale / 100));
        ySize = PApplet.parseInt(ySize * (uiScale / 100));
        if (mode == 1) {
            fullMode = true;
        } else if (mode == 2) {
            compactMode = true;
        } else {
            wheelMode = true;
        }

        if (compactMode) xSize = xSize / 2;
        if (wheelMode) {
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
        if (!compactMode) {
            wheelImg = loadImage(pathPrefix + "wheel.png");
        } else {
            wheelImg = loadImage(pathPrefix + "wheelCompact.png");
        }
        //draw controllers on screen
        if (fullMode) {
            drawControllers();
        } else if (compactMode) {
            drawControllersCompact();
        } else {
            drawControllersWheel();
        }

        createUDP();

        if (debugging) println("Setup complete.");
    }

//    __       _ _
//   / _|     | | |
//  | |_ _   _| | |
//  |  _| | | | | |
//  | | | |_| | | |
//  |_|  \__,_|_|_|

    public void drawControllers() {


        // Create some dials and gauges on screen
        cp5 = new ControlP5(this);

        // change the default font to Verdana
        PFont p = createFont("monospaced", 14, true);
        PFont p1 = createFont("monospaced.bold", 24, true);
        PFont p2 = createFont("consolas", 116, true);

        cp5.setFont(p);
        // change the original colors
        //cp5.setColorForeground(0xffd0eff2);
        cp5.setColorForeground(fgColor);
        cp5.setColorBackground(bgColor);
        cp5.setColorValueLabel(lblColor);
        cp5.setColorActive(activeColor);

        cp5.addKnob("speed")
                .lock()
                .setRadius(60)
                .setPosition(210, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, maxSpeed);
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

        cp5.addKnob("rpm")
                .lock()
                .setRadius(60)
                .setPosition(777, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, maxRevs);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);
        cp5.getController("rpm").getValueLabel().setFont(p1);

        cp5.addSlider("clutch")
                .lock()
                .setColorForeground((colorPedals) ? (BLUE) : WHITE)
                .setSize(25, 120)
                .setPosition(485, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0, 1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);

        cp5.addSlider("brakes")
                .lock()
                .setColorForeground((colorPedals) ? (RED) : WHITE)
                .setSize(25, 120)
                .setPosition(525, 14)
                .setValue(0)
                .setRange(0, 1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .lock()
                .setColorForeground((colorPedals) ? (GREEN) : WHITE)
                .setSize(25, 120)
                .setPosition(565, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0, 1);
        cp5.getController("throttle").getValueLabel().setVisible(false);
        cp5.getController("throttle").getCaptionLabel().setVisible(false);

        cp5.addTextlabel("gearLabel")
                .lock()
                .setSize(0, 0)
                .setPosition(375, 14)
                .setValue("N");
        cp5.getController("gearLabel").getCaptionLabel().setVisible(false);
        cp5.getController("gearLabel").getValueLabel().setFont(p2);

        cp5.addTextlabel("lapTime")
                .lock()
                .setSize(65, 25)
                .setPosition(476, 145)
                .setStringValue("00:00:00")
                .setColorBackground(color(0, 0, 0))
                .setVisible(true);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);
        cp5.getController("lapTime").getValueLabel().setFont(p1);

        cp5.addTextlabel("lbldistance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(720, 135)
                .setValue("dist (m)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showDist);
        cp5.getController("lbldistance").getCaptionLabel().setVisible(false);
        //cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("distance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(705, 145)
                .setValue("00000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showDist);
        cp5.getController("distance").getCaptionLabel().setVisible(false);
        cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblmaxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(310, 135)
                .setValue("max (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showMax);
        cp5.getController("lblmaxSpd").getCaptionLabel().setVisible(false);
        //cp5.getController("lblmaxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("maxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(312, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showMax);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);
        cp5.getController("maxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblavgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(135, 135)
                .setValue("avg (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showAvg);
        cp5.getController("lblavgSpd").getCaptionLabel().setVisible(true);
        //cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("avgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(137, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showAvg);
        cp5.getController("avgSpd").getCaptionLabel().setVisible(true);
        cp5.getController("avgSpd").getValueLabel().setFont(p1);
        cp5.addNumberbox("posx")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 100)
                .setValue(0)
                .setVisible(false);

        cp5.addNumberbox("posy")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 130)
                .setValue(0)
                .setVisible(false);
        cp5.getController("posy").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(true);
        cp5.getController("suspFL").getValueLabel().setVisible(false);
        cp5.getController("suspFL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(true);
        cp5.getController("suspFR").getValueLabel().setVisible(false);
        cp5.getController("suspFR").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(true);
        cp5.getController("suspAL").getValueLabel().setVisible(false);
        cp5.getController("suspAL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(true);
        cp5.getController("suspAR").getValueLabel().setVisible(false);
        cp5.getController("suspAR").getCaptionLabel().setVisible(false);

        if (debugging) println("DrawControllers complete.");
    }

//                                        _
//                                       | |
//    ___ ___  _ __ ___  _ __   __ _  ___| |_
//   / __/ _ \| '_ ` _ \| '_ \ / _` |/ __| __|
//  | (_| (_) | | | | | | |_) | (_| | (__| |_
//   \___\___/|_| |_| |_| .__/ \__,_|\___|\__|
//                      | |
//                      |_|

    public void drawControllersCompact() {


        // Create some dials and gauges on screen

        cp5 = new ControlP5(this);

        // change the default font
        PFont p = createFont("monospaced", 14, true);
        PFont p1 = createFont("monospaced.bold", 24, true);
        PFont p2 = createFont("consolas", 86, true);

        cp5.setFont(p);
        // change the original colors
        //cp5.setColorForeground(0xffd0eff2);
        cp5.setColorForeground(fgColor);
        cp5.setColorBackground(bgColor);
        cp5.setColorValueLabel(lblColor);  //0a0a0a will hide it
        //cp5.setColorValue(0xffebf8ff);
        cp5.setColorActive(activeColor);

        cp5.addKnob("speed")
                .lock()
                .setRadius(60)
                .setPosition(30, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setVisible(true)
                .setRange(0, maxSpeed);
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

        cp5.addKnob("rpm")
                .lock()
                .setRadius(60)
                .setPosition(400, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, maxRevs)
                .setVisible(true);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);
        cp5.getController("rpm").getValueLabel().setFont(p1);

        cp5.addSlider("clutch")
                .lock()
                .setColorForeground((colorPedals) ? (BLUE) : WHITE)
                .setSize(25, 120)
                .setPosition(200, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setVisible(false)
                .setRange(0, 1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);

        cp5.addSlider("brakes")
                .lock()
                .setColorForeground((colorPedals) ? (RED) : WHITE)
                .setSize(25, 120)
                .setPosition(175, 14)
                .setValue(0)
                .setRange(0, 1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .lock()
                .setColorForeground((colorPedals) ? (GREEN) : WHITE)
                .setSize(25, 120)
                .setPosition(350, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0, 1);
        cp5.getController("throttle").getValueLabel().setVisible(false);
        cp5.getController("throttle").getCaptionLabel().setVisible(false);

        cp5.addTextlabel("gearLabel")
                .lock()
                .setSize(0, 0)
                .setPosition(248, 30)
                .setValue("N")
                .setVisible(showGear);
        cp5.getController("gearLabel").getCaptionLabel().setVisible(false);
        cp5.getController("gearLabel").getValueLabel().setFont(p2);

        cp5.addTextlabel("lapTime")
                .lock()
                .setSize(65, 25)
                .setPosition(213, 139)
                .setStringValue("00:00:00")
                .setColorBackground(color(0, 0, 0))
                .setVisible(true);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);
        cp5.getController("lapTime").getValueLabel().setFont(p1);

        cp5.addTextlabel("lbldistance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(425, 135)
                .setValue("dist (m)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showDist);
        cp5.getController("lbldistance").getCaptionLabel().setVisible(false);
        //cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("distance")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(410, 145)
                .setValue("00000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showDist);
        cp5.getController("distance").getCaptionLabel().setVisible(false);
        cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblmaxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(340, 135)
                .setValue("max (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lblmaxSpd").getCaptionLabel().setVisible(false);
        //cp5.getController("lblmaxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("maxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(320, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);
        cp5.getController("maxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblavgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(46, 135)
                .setValue("avg (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showAvg);
        cp5.getController("lblavgSpd").getCaptionLabel().setVisible(true);
        //cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("avgSpd")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(50, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(showAvg);
        cp5.getController("avgSpd").getCaptionLabel().setVisible(true);
        cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addNumberbox("posx")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 100)
                .setValue(0)
                .setVisible(false);

        cp5.addNumberbox("posy")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 130)
                .setValue(0)
                .setVisible(false);
        cp5.getController("posy").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspFL").getValueLabel().setVisible(false);
        cp5.getController("suspFL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspFR").getValueLabel().setVisible(false);
        cp5.getController("suspFR").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspAL").getValueLabel().setVisible(false);
        cp5.getController("suspAL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspAR").getValueLabel().setVisible(false);
        cp5.getController("suspAR").getCaptionLabel().setVisible(false);

        if (debugging) println("DrawControllersCompact complete.");
    }

//
// __          ___               _ __  __           _
// \ \        / / |             | |  \/  |         | |
//  \ \  /\  / /| |__   ___  ___| | \  / | ___   __| | ___
//   \ \/  \/ / | '_ \ / _ \/ _ \ | |\/| |/ _ \ / _` |/ _ \
//    \  /\  /  | | | |  __/  __/ | |  | | (_) | (_| |  __/
//     \/  \/   |_| |_|\___|\___|_|_|  |_|\___/ \__,_|\___|
//

    public void drawControllersWheel() {


        // Create some dials and gauges on screen

        cp5 = new ControlP5(this);

        // change the default font
        PFont p = createFont("monospaced", 14, true);
        PFont p1 = createFont("monospaced.bold", 24, true);
        PFont p2 = createFont("consolas", 86, true);

        cp5.setFont(p);
        // change the original colors
        //cp5.setColorForeground(0xffd0eff2);
        cp5.setColorForeground(fgColor);
        cp5.setColorBackground(bgColor);
        cp5.setColorValueLabel(lblColor);  //0a0a0a will hide it
        //cp5.setColorValue(0xffebf8ff);
        cp5.setColorActive(activeColor);

        cp5.addKnob("speed")
                .lock()
                .setRadius(60)
                .setPosition(30, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setVisible(false)
                .setRange(0, maxSpeed);
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

        cp5.addKnob("rpm")
                .lock()
                .setRadius(60)
                .setPosition(400, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, maxRevs)
                .setVisible(false);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);
        cp5.getController("rpm").getValueLabel().setFont(p1);

        cp5.addSlider("clutch")
                .lock()
                .setColorForeground((colorPedals) ? (BLUE) : WHITE)
                .setSize(25, 120)
                .setPosition(200, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setVisible(false)
                .setRange(0, 1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);

        cp5.addSlider("brakes")
                .lock()
                .setColorForeground((colorPedals) ? (RED) : WHITE)
                .setSize(25, 120)
                .setPosition(25, 14)
                .setValue(0)
                .setRange(0, 1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .lock()
                .setColorForeground((colorPedals) ? (GREEN) : WHITE)
                .setSize(25, 120)
                .setPosition(200, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0, 1);
        cp5.getController("throttle").getValueLabel().setVisible(false);
        cp5.getController("throttle").getCaptionLabel().setVisible(false);

        cp5.addTextlabel("gearLabel")
                .lock()
                .setSize(0, 0)
                .setPosition(248, 30)
                .setValue("N")
                .setVisible(false);
        cp5.getController("gearLabel").getCaptionLabel().setVisible(false);
        cp5.getController("gearLabel").getValueLabel().setFont(p2);

        cp5.addTextlabel("lapTime")
                .lock()
                .setSize(65, 25)
                .setPosition(213, 139)
                .setStringValue("00:00:00")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);
        cp5.getController("lapTime").getValueLabel().setFont(p1);

        cp5.addTextlabel("lbldistance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(425, 135)
                .setValue("dist (m)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lbldistance").getCaptionLabel().setVisible(false);
        //cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("distance")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(410, 145)
                .setValue("00000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("distance").getCaptionLabel().setVisible(false);
        cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblmaxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(340, 135)
                .setValue("max (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lblmaxSpd").getCaptionLabel().setVisible(false);
        //cp5.getController("lblmaxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("maxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(320, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);
        cp5.getController("maxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblavgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(46, 135)
                .setValue("avg (km/h)")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lblavgSpd").getCaptionLabel().setVisible(true);
        //cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("avgSpd")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(50, 145)
                .setValue("000.0")
                .setColorBackground(color(0, 0, 0))
                .setVisible(false);
        cp5.getController("avgSpd").getCaptionLabel().setVisible(true);
        cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addNumberbox("posx")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 100)
                .setValue(0)
                .setVisible(false);

        cp5.addNumberbox("posy")
                .lock()
                .setSize(75, 25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(780, 130)
                .setValue(0)
                .setVisible(false);
        cp5.getController("posy").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspFL").getValueLabel().setVisible(false);
        cp5.getController("suspFL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 14)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspFR").getValueLabel().setVisible(false);
        cp5.getController("suspFR").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAL")
                .lock()
                .setSize(15, 50)
                .setPosition(949, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspAL").getValueLabel().setVisible(false);
        cp5.getController("suspAL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAR")
                .lock()
                .setSize(15, 50)
                .setPosition(1034, 95)
                .setValue(0)
                .setRange(-3, 40)
                .setVisible(false);
        cp5.getController("suspAR").getValueLabel().setVisible(false);
        cp5.getController("suspAR").getCaptionLabel().setVisible(false);

        if (debugging) println("DrawControllersWheel complete.");
    }

//                     _
//                    (_)
//   _ __ ___  ___ ___ ___   _____
//  | '__/ _ \/ __/ _ \ \ \ / / _ \
//  | | |  __/ (_|  __/ |\ V /  __/
//  |_|  \___|\___\___|_| \_/ \___|
//


    public void receive(byte[] data, String ip, int portRX) {
        if (debugging) fullOutput(data);

        // Function to output all the game data received

        //Bitwise & ([pos] & 0xff), Takes byte and multiplies it by 00000000 00000000 11111111, so that the only thing remaining are the last 8 bits.
        //Float.intBitsToFloat converts binary to a float integer and makes it equal to the stated vars.
        pos = 0;
        float tTime = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // Lap time
        pos = 4;
        float lapTime = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // Distance
        pos = 8;
        float distance = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        if (distance < 0) distance = 0;
        //Pos X
        pos = 16;
        float posx = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Pos Y
        pos = 20;
        float posy = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // Speed, *3.6 for Km/h
        pos = 28;
        float speed = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24)) * 3.6f;
        //Suspension travel aft left
        pos = 68;
        float suspAL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Suspension travel aft right
        pos = 72;
        float suspAR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Suspension travel fwd left
        pos = 76;
        float suspFL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Suspension travel fwd right
        pos = 80;
        float suspFR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Wheel speed aft left
        pos = 100;
        float wspAL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Wheel speed aft right
        pos = 104;
        float wspAR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Wheel speed fwd left
        pos = 108;
        float wspFL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Wheel speed fwd right
        pos = 112;
        float wspFR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Throttle 0-1
        pos = 116;
        float throttle = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Brakes 0-1
        pos = 124;
        float brakes = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //Clutch 0-1
        pos = 128;
        float clutch = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        //steering
        pos = 120;
        float steering = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // Gear, neutral = 0
        pos = 132;
        float gear = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // gForceX
        pos = 136;
        float gForce_X = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // gForceY
        pos = 140;
        float gForce_Y = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // Current lap, starts at 0
        pos = 144;
        float cLap = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24));
        // RPM, requires *10 for realistic values
        pos = 148;
        float rpm = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos + 1] & 0xff) << 8) | ((data[pos + 2] & 0xff) << 16) | ((data[pos + 3] & 0xff) << 24)) * 10;
        // Debug the received values

        if (debugging) {
            gameDataOutput(tTime, lapTime, speed, gear, cLap, rpm, gForce_X, steering);
        }

        updateControllers(tTime, lapTime, distance, posx, posy, speed, suspAL,
                suspAR, suspFL, suspFR, wspAL, wspAR, wspFL, wspFR,
                throttle, brakes, clutch, steering, gear, gForce_X,
                gForce_Y, cLap, rpm);
        if (debugging) println("Receive complete.");
    }

    public void updateControllers(float utTime, float ulapTime, float udistance, float uposx, float uposy, float uspeed, float ususpAL, float ususpAR, float ususpFL,
                                  float ususpFR, float uwspAL, float uwspAR, float uwspFL, float uwspFR, float uthrottle, float ubrakes, float uclutch, float usteering, float ugear, float ugForce_X, float ugForce_Y, float ucLap, float urpm) {

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
        if (fullMode) {

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


        } else if (compactMode && !isWindowBeingDragged) {
            //Black panels for keying
            fill(BLACK);
            stroke(0);
            //ellipse(275,75,85,85);
            image(kPanel, 0, 136);
            //rect(0,136,550,38);
            //Clutch Lights
            if (!fullMode & clutchD > clutchSens & clutchIndicator) {
                image(bLightOn, 175, 143, 25, 25);
                image(bLightOn, 350, 143, 25, 25);
            } else if (clutchIndicator) {
                image(bLightOff, 175, 143, 25, 25);
                image(bLightOff, 350, 143, 25, 25);
            }
        } else {
            if (!fullMode & clutchD > clutchSens & clutchIndicator) {
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
        if (fullMode) {
            translate(684, 75);
            rotate(steeringOutput);
            image(wheelImg, -50, -50, 100, 100);
        } else if (compactMode) {
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
        udpRX = new UDP(this, portRX, ip);
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

    private static String timeConversion(int centiseconds) {

        final int SECONDS_IN_A_MINUTE = 60;
        final int CENTISECONDS_IN_A_SECOND = 100;

        int seconds = centiseconds / CENTISECONDS_IN_A_SECOND;
        centiseconds -= seconds * CENTISECONDS_IN_A_SECOND;

        int minutes = seconds / SECONDS_IN_A_MINUTE;
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        return nf(minutes, 2) + ":" + nf(seconds, 2) + ":" + nf(centiseconds, 2);
    }

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
        settingsin[18] = String.valueOf(winLoc.x + (width / 2));
        settingsin[19] = String.valueOf(winLoc.y);
        saveStrings(settingsPath, settingsin);
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

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"DiRT_Telemetry_Tool"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
