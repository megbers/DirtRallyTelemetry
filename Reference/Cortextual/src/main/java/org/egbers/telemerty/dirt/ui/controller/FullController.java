package org.egbers.telemerty.dirt.ui.controller;

import controlP5.ControlP5;
import org.egbers.telemerty.dirt.ui.DashBoardApplet;
import org.egbers.telemerty.dirt.ui.UISettings;
import processing.core.PFont;

public class FullController extends Controller {
    private ControlP5 cp5;

    @Override
    public ControlP5 drawControls(DashBoardApplet applet) {
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


}