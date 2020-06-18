package org.egbers.telemerty.dirt.ui.controller;

import controlP5.ControlP5;
import org.egbers.telemerty.dirt.ui.DashBoardApplet;

public abstract class Controller {
    public abstract ControlP5 drawControls(DashBoardApplet applet);
    //public abstract void draw(DashBoardApplet applet);
}
