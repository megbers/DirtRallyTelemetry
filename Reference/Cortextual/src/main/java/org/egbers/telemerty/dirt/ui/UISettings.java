package org.egbers.telemerty.dirt.ui;

public class UISettings {
    private int bgColor;
    private int fgColor;
    private int lblColor;
    private int activeColor;

    private boolean gballFlash;
    private int wheelRot;
    private boolean colorGearChange;
    private boolean colorPedals;
    private boolean colorSpeedo;
    private boolean colorRevs;
    private boolean showMax;
    private boolean showAvg;
    private boolean showDist;
    private boolean colorSusp;
    private boolean colorGball;
    private boolean useJoystick;
    private int maxRevs;
    private int maxSpeed;
    private float uiScale;
    private int mode;
    private boolean cKey;
    private boolean clutchIndicator;
    private int winPosX;
    private int winPosY;
    private boolean borderless;
    private boolean showGTrace;
    private int traceLength;
    private boolean alwaysTop;
    private boolean showGear;

    private boolean fullMode;
    private boolean compactMode;
    private boolean wheelMode;

    private float clutchSens;
    private int gearColorDuration;

    public UISettings(String[] settingsin, int bgColor, int fgColor, int lblColor, int activeColor) {
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
        useJoystick = Boolean.valueOf(settingsin[11]);
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
        showGear = true;

        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.lblColor = lblColor;
        this.activeColor = activeColor;

        if (mode == 1) {
            fullMode = true;
        } else if (mode == 2) {
            compactMode = true;
        } else {
            wheelMode = true;
        }
        clutchSens = 0.2f;
        gearColorDuration = 250;
    }

    public boolean getGballFlash() {
        return gballFlash;
    }

    public int getWheelRot() {
        return wheelRot;
    }

    public boolean getColorGearChange() {
        return colorGearChange;
    }

    public boolean getColorPedals() {
        return colorPedals;
    }

    public boolean getColorSpeedo() {
        return colorSpeedo;
    }

    public boolean getColorRevs() {
        return colorRevs;
    }

    public boolean getShowMax() {
        return showMax;
    }

    public boolean getShowAvg() {
        return showAvg;
    }

    public boolean getShowDist() {
        return showDist;
    }

    public boolean getColorSusp() {
        return colorSusp;
    }

    public boolean getColorGball() {
        return colorGball;
    }

    public boolean getUseJoystick() {
        return useJoystick;
    }

    public int getMaxRevs() {
        return maxRevs;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public float getUiScale() {
        return uiScale;
    }

    public int getMode() {
        return mode;
    }

    public boolean getcKey() {
        return cKey;
    }

    public boolean getClutchIndicator() {
        return clutchIndicator;
    }

    public int getWinPosX() {
        return winPosX;
    }

    public int getWinPosY() {
        return winPosY;
    }

    public boolean getBorderless() {
        return borderless;
    }

    public boolean getShowGTrace() {
        return showGTrace;
    }

    public int getTraceLength() {
        return traceLength;
    }

    public boolean getAlwaysTop() {
        return alwaysTop;
    }

    public boolean getShowGear() {
        return showGear;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getFgColor() {
        return fgColor;
    }

    public int getLblColor() {
        return lblColor;
    }

    public int getActiveColor() {
        return activeColor;
    }

    public boolean getFullMode() {
        return fullMode;
    }

    public boolean getCompactMode() {
        return compactMode;
    }

    public boolean getWheelMode() {
        return wheelMode;
    }

    public float getClutchSens() {
        return clutchSens;
    }

    public int getGearColorDuration() {
        return gearColorDuration;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Settings Loaded from file: ").append("\n")
            .append("G-Ball Flash: " + gballFlash + " Wheel Rotation: " + wheelRot + " Color Gear Change: " + colorGearChange).append("\n")
            .append("Color Pedals: " + colorPedals + " Color Speedo: " + colorSpeedo + " Color Revs: " + colorRevs).append("\n")
            .append("Show Max: " + showMax + " Show Average: " + showAvg + " Show Distance: " + showDist).append("\n")
            .append("Color Suspension: " + colorSusp + " Color G-Ball: " + colorGball).append("\n")
            .append(" Max Revs: " + maxRevs + " Max Speed: " + maxSpeed + " UI Scale: " + uiScale + "%");
        return builder.toString();
    }
}
