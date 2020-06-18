package org.egbers.telemerty.dirt.ui.controller;

import controlP5.ControlP5;
import org.egbers.telemerty.dirt.ui.DashBoardApplet;
import org.egbers.telemerty.dirt.ui.UISettings;
import processing.core.PFont;
import processing.core.PImage;

public class FullController extends Controller {
    private ControlP5 cp5;
    private PImage wheelImg;

    public FullController(DashBoardApplet applet) {
        super(applet);
        wheelImg = applet.loadImage(applet.pathPrefix + "wheel.png");
    }

    @Override
    public ControlP5 drawControls() {
        // Create some dials and gauges on screen
        cp5 = new ControlP5(applet);
        UISettings settings = applet.getSettings();

        // change the default font to Verdana
        PFont p = applet.createFont("monospaced", 14, true);
        PFont p1 = applet.createFont("monospaced.bold", 24, true);
        PFont p2 = applet.createFont("consolas", 116, true);

        cp5.setFont(p);
        // change the original colors
        //cp5.setColorForeground(0xffd0eff2);
        cp5.setColorForeground(settings.getFgColor());
        cp5.setColorBackground(settings.getBgColor());
        cp5.setColorValueLabel(settings.getLblColor());
        cp5.setColorActive(settings.getActiveColor());

        cp5.addKnob("speed")
                .lock()
                .setRadius(60)
                .setPosition(210, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, settings.getMaxSpeed());
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

        cp5.addKnob("rpm")
                .lock()
                .setRadius(60)
                .setPosition(777, 15)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0, settings.getMaxRevs());
        cp5.getController("rpm").getCaptionLabel().setVisible(false);
        cp5.getController("rpm").getValueLabel().setFont(p1);

        cp5.addSlider("clutch")
                .lock()
                .setColorForeground(settings.getColorPedals() ? (applet.BLUE) : applet.WHITE)
                .setSize(25, 120)
                .setPosition(485, 14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0, 1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);

        cp5.addSlider("brakes")
                .lock()
                .setColorForeground(settings.getColorPedals() ? (applet.RED) : applet.WHITE)
                .setSize(25, 120)
                .setPosition(525, 14)
                .setValue(0)
                .setRange(0, 1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .lock()
                .setColorForeground(settings.getColorPedals() ? (applet.GREEN) : applet.WHITE)
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
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(true);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);
        cp5.getController("lapTime").getValueLabel().setFont(p1);

        cp5.addTextlabel("lbldistance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(720, 135)
                .setValue("dist (m)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowDist());
        cp5.getController("lbldistance").getCaptionLabel().setVisible(false);
        //cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("distance")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(705, 145)
                .setValue("00000.0")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowDist());
        cp5.getController("distance").getCaptionLabel().setVisible(false);
        cp5.getController("distance").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblmaxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(310, 135)
                .setValue("max (km/h)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowMax());
        cp5.getController("lblmaxSpd").getCaptionLabel().setVisible(false);
        //cp5.getController("lblmaxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("maxSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(312, 145)
                .setValue("000.0")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowMax());
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);
        cp5.getController("maxSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("lblavgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(135, 135)
                .setValue("avg (km/h)")
                .setColorBackground(applet.color(0, 0, 0))
                .setVisible(settings.getShowAvg());
        cp5.getController("lblavgSpd").getCaptionLabel().setVisible(true);
        //cp5.getController("avgSpd").getValueLabel().setFont(p1);

        cp5.addTextlabel("avgSpd")
                .lock()
                .setSize(65, 25)
                //.setDecimalPrecision(1)
                .setPosition(137, 145)
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

        //if (debugging) println("DrawControllers complete.");
        return cp5;
    }

    @Override
    public void draw() {
        UISettings settings = applet.getSettings();
        //Draw GBall
        if (!applet.isWindowBeingDragged) {
            applet.ellipseMode(applet.CENTER);
            applet.stroke(0);

            //if gballFlash is turned on
            //Flash G-Ball enclosure red if gforce > 15
            if (((applet.abs(applet.gForceX) > 15) || (applet.abs(applet.gForceX) > 15)) && (settings.getGballFlash())) {
                applet.fill(applet.RED);
                // red outer ellipse for G-Ball
                //with grey central ellipse
                applet.ellipse(96, 75, 120, 120);
                applet.fill(applet.DARKGREY);
                applet.ellipse(96, 75, 70, 70);
            } else if (((applet.abs(applet.gForceX) > 10) || (applet.abs(applet.gForceX) > 10)) && (settings.getGballFlash())) {
                applet.fill(applet.ORANGE);
                // orange outer ellipse for G-Ball
                //with grey central ellipse
                applet.ellipse(96, 75, 120, 120);
                applet.fill(applet.DARKGREY);
                applet.ellipse(96, 75, 70, 70);
            } else {
                applet.fill(applet.DARKGREY);
                //background ellipse for G-Ball
                applet.ellipse(96, 75, 120, 120);
            }
            //Black lines on G-Ball
            applet.fill(applet.DARKGREY);
            applet.ellipse(96, 75, 70, 70);
            //stroke(255);
            applet.fill(255, 0);
            applet.ellipse(96, 75, 70, 70);
            applet.line(96, 15, 96, 135);
            applet.line(36, 75, 156, 75);
            applet.fill(255, 0);

            //Draw Trace of previous 10 GBall positions
            applet.fill(applet.WHITE);
            applet.noStroke();
            applet.stroke(255, 255, 255, 200);
            applet.noFill();
            if (settings.getShowGTrace()) {
                //curveTightness(.5);
                applet.strokeWeight(2);
                applet.beginShape();
                applet.curveVertex(applet.gballOld[0].x, applet.gballOld[0].y);
                for (int i = 1; i < settings.getTraceLength() - 1; i++) {

                    //ellipse(gballOld[i].x, gballOld[i].y, 5, 5);
                    //line(gballOld[i-1].x, gballOld[i-1].y,gballOld[i].x, gballOld[i].y);
                    if (applet.dist(applet.gballOld[i].x, applet.gballOld[i].y, applet.gballOld[i - 1].x, applet.gballOld[i - 1].y) > 2) {
                        applet.curveVertex(applet.gballOld[i].x, applet.gballOld[i].y);
                    }

                    //moving avg
                    //float avgX = (gballOld[i].x + gballOld[i+1].x + gballOld[i+2].x) / 3;
                    //float avgY = (gballOld[i].y + gballOld[i+1].y + gballOld[i+2].y) /3;
                    //curveVertex(avgX, avgY);
                }
                applet.curveVertex(applet.gballOld[settings.getTraceLength()].x, applet.gballOld[settings.getTraceLength()].y);
                applet.endShape();
            }
            //Draw G-Ball
            applet.gballNew.x = applet.map(applet.gForceX, -20, 20, -40, 40) + 96;
            applet.gballNew.y = applet.map(applet.gForceY, -20, 20, -40, 40) + 75;
            //if the G-Ball is going to jump more than 5 pixels in one frame
            //don't let it
            //if (dist(gballNew.x, gballNew.y, gballOld.x, gballOld.y) > 25) {
            //  gballNew.x = gballOld.x;
            //  gballNew.y = gballOld.y;
            //}
            //shading for G-Ball
            //color shaded or white depending on settings

            if (settings.getColorGball()) {
                //shadow
                //fill(0, 0, 0, 100);
                //noStroke();
                //ellipse(gballNew.x, gballNew.y, 20, 20);
                for (int i = 1; i < 17; i++) {
                    applet.noFill();
                    applet.stroke(applet.color(180, 180 - (180 / 16) * i, 180 - (180 / 16) * i));
                    applet.ellipse(applet.gballNew.x, applet.gballNew.y, i, i);
                }
            } else {
                applet.fill(applet.WHITE);
                applet.stroke(0);
                applet.ellipse(applet.gballNew.x, applet.gballNew.y, 16, 16);
            }

            //Differential draw
            applet.rectMode(applet.CENTER);
            applet.stroke(0);
            applet.fill(255);
            applet.rect(1000 + applet.fdiff, 40, 10, 30);
            applet.rect(1000, 80 + applet.cdiff, 30, 10);
            applet.rect(1000 + applet.rdiff, 120, 10, 30);
        }
        if (settings.getShowGTrace()) {
            //Update GBall previous positions
            for (int i = settings.getTraceLength() - 1; i > -1; i--) {
                applet.gballOld[i + 1].x = applet.gballOld[i].x;
                applet.gballOld[i + 1].y = applet.gballOld[i].y;
            }
            applet.gballOld[0].x = applet.gballNew.x;
            applet.gballOld[0].y = applet.gballNew.y;
        }
    }

    @Override
    public void drawWheel(){
        applet.pushMatrix();
        applet.translate(684, 75);
        applet.rotate(applet.steeringOutput);
        applet.image(wheelImg, -50, -50, 100, 100);
        applet.popMatrix();
    }

}
