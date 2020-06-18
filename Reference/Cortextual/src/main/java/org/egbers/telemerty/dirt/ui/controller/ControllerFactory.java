package org.egbers.telemerty.dirt.ui.controller;

public class ControllerFactory {
    public static Controller createController(int mode) {
        if (mode == 1) {
            return new FullController();
        } else if (mode == 2) {
            return new CompactController();
        } else {
            return new WheelController();
        }
    }

}
