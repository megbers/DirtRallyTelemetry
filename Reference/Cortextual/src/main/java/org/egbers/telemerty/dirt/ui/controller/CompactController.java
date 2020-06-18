package org.egbers.telemerty.dirt.ui.controller;

import controlP5.ControlP5;
import org.egbers.telemerty.dirt.ui.DashBoardApplet;
import org.egbers.telemerty.dirt.ui.UISettings;
import processing.core.PFont;
import processing.core.PImage;

public class CompactController extends Controller {
    private ControlP5 cp5;
    private PImage kPanel, bLightOn, bLightOff, wheelImg;

    public CompactController(DashBoardApplet applet) {
        super(applet);
        kPanel = applet.loadImage(applet.pathPrefix + "kPanel.png");
        bLightOn = applet.loadImage(applet.pathPrefix + "bLightOn.png");
        bLightOff = applet.loadImage(applet.pathPrefix + "bLightOff.png");
        wheelImg = applet.loadImage(applet.pathPrefix + "wheelCompact.png");
    }

    @Override
    public ControlP5 drawControls() {
        // Create some dials and gauges on screen

        cp5 = new ControlP5(applet);
        UISettings settings = applet.getSettings();

        // change the default font
        PFont p = applet.createFont("monospaced", 14, true);
        PFont p1 = applet.createFont("monospaced.bold", 24, true);
        PFont p2 = applet.createFont("consolas", 86, true);

        cp5.setFont(p);
        // change the original colors
        //cp5.setColorForeground(0xffd0eff2);
        cp5.setColorForeground(settings.getFgColor());
        cp5.setColorBackground(settings.getBgColor());
        cp5.setColorValueLabel(settings.getLblColor());  //0a0a0a will hide it
        //cp5.setColorValue(0xffebf8ff);
        cp5.setColorActive(settings.getActiveColor());

        cp5.addKnob("speed")
                .lock()
                .setRadius(60)
                .setPosition(30, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setVisible(true)
                .setRange(0, settings.getMaxSpeed());
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

        cp5.addKnob("rpm")
                .lock()
                .setRadius(60)
                .setPosition(400, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, settings.getMaxRevs())
                .setVisible(true);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);
        cp5.getController("rpm").getValueLabel().setFont(p1);

        cp5.addSlider("clutch")
                .lock()
                .setColorForeground(settings.getColorPedals() ? (applet.BLUE) : applet.WHITE)
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
                .setColorForeground(settings.getColorPedals() ? (applet.RED) : applet.WHITE)
                .setSize(25, 120)
                .setPosition(175, 14)
                .setValue(0)
                .setRange(0, 1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .lock()
                .setColorForeground(settings.getColorPedals() ? (applet.GREEN) : applet.WHITE)
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
                .setVisible(settings.getShowGear());
        cp5.getController("gearLabel").getCaptionLabel().setVisible(false);
        cp5.getController("gearLabel").getValueLabel().setFont(p2);

        cp5.addTextlabel("lapTime")
                .lock()
                .setSize(65, 25)
                .setPosition(213, 139)
                .setStringValue("00:00:00")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(true);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);
        cp5.getController("lapTime").getValueLabel().setFont(p1);

        cp5.addTextlabel("lbldistance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(425, 135)
                .setValue("dist (m)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowDist());
        cp5.getController("lbldistance").getCaptionLabel().setVisible(false);
        //cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("distance")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(410, 145)
                .setValue("00000.0")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowDist());
        cp5.getController("distance").getCaptionLabel().setVisible(false);
        cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblmaxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(340, 135)
                .setValue("max (km/h)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(false);
        cp5.getController("lblmaxSpd").getCaptionLabel().setVisible(false);
        //cp5.getController("lblmaxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("maxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(320, 145)
                .setValue("000.0")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(false);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);
        cp5.getController("maxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblavgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(46, 135)
                .setValue("avg (km/h)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowAvg());
        cp5.getController("lblavgSpd").getCaptionLabel().setVisible(true);
        //cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("avgSpd")
                .lock()
                .setSize(0, 0)
                //.setDecimalPrecision(1)
                .setPosition(50, 145)
                .setValue("000.0")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowAvg());
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

        //if (debugging) println("DrawControllersCompact complete.");
        return cp5;
    }

    @Override
    public void draw() {
        UISettings settings = applet.getSettings();
        //Black panels for keying
        applet.fill(applet.BLACK);
        applet.stroke(0);
        //ellipse(275,75,85,85);
        applet.image(kPanel, 0, 136);
        //rect(0,136,550,38);
        //Clutch Lights
        if (!settings.getFullMode() & applet.clutchD > settings.getClutchSens() & settings.getClutchIndicator()) {
            applet.image(bLightOn, 175, 143, 25, 25);
            applet.image(bLightOn, 350, 143, 25, 25);
        } else if (settings.getClutchIndicator()) {
            applet.image(bLightOff, 175, 143, 25, 25);
            applet.image(bLightOff, 350, 143, 25, 25);
        }
    }

    @Override
    public void drawWheel() {
        applet.pushMatrix();
        applet.translate(275, 75);
        applet.rotate(applet.steeringOutput);
        applet.image(wheelImg, -60, -60, 120, 120);
        applet.popMatrix();
    }
}
