package org.egbers.telemerty.dirt.ui.controller;

import org.egbers.telemerty.dirt.ui.DashBoardApplet;

public class ControllerFactory {
    public static Controller createController(DashBoardApplet applet, int mode) {
        if (mode == 1) {
            return new FullController(applet);
        } else if (mode == 2) {
            return new CompactController(applet);
        } else {
            return new WheelController(applet);
        }
    }

}
