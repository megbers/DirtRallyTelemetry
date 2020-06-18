package org.egbers.telemerty.dirt.ui.controller;

import controlP5.ControlP5;
import org.egbers.telemerty.dirt.ui.DashBoardApplet;

public abstract class Controller {
    protected DashBoardApplet applet;

    public Controller(DashBoardApplet applet) {
        this.applet = applet;
    }

    public abstract ControlP5 drawControls();
    public abstract void draw();
    public abstract void drawWheel();

    public void colorGear() {
        if (applet.currentGear == 10 || applet.currentGear < 0) {
            //make reverse lower than the other gears for the shifting-up logic
            applet.currentGear = -10;
            applet.gearValue = "R";
        } else if (applet.currentGear == 0) {
            applet.gearValue = "N";
        } else {
            applet.gearValue = String.valueOf(applet.currentGear);
            applet.gearValue = applet.gearValue.substring(0, 1);
        }

        //check direction of gear change and time stamp it
        if (applet.previousGear > applet.currentGear) {
            applet.timer = applet.millis();
            applet.changedUp = false;
        } else if (applet.previousGear < applet.currentGear) {
            applet.timer = applet.millis();
            applet.changedUp = true;
        }

        //Checks that gear change was recent
        if ((applet.timer > 0) && (applet.millis() - applet.timer < applet.getSettings().getGearColorDuration())) {
            applet.gearChanged = true;
        } else {
            applet.gearChanged = false;
        }

        // if you're in neutral or reverse the gear indicator is red
        // if you're shifting up from any gear other than neutral it's green, down it's red
        // neutral has to be ignored because H-Shifters go to it between gears and
        // everything becomes an upshift from neutral

        if (applet.getSettings().getColorGearChange()) {
            int color;
            if ((applet.gearValue == "R") || (applet.gearValue == "N")) {
                color = applet.RED;
            } else if ((applet.changedUp) && (applet.gearChanged)) {
                color = applet.GREEN;
            } else if ((!applet.changedUp) && (applet.gearChanged)) {
                color = applet.RED;
            } else {
                color = applet.WHITE;
            }
            applet.setFieldColor("gearLabel", color);
        }

        if (applet.currentGear != 0) applet.previousGear = applet.currentGear;
        //if (debugging) println("ColorGear complete.");
    }
}
