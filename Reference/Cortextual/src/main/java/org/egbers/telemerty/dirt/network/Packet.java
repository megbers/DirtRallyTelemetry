package org.egbers.telemerty.dirt.network;

public class Packet {
    private float utTime;
    private float ulapTime;
    private float udistance;
    private float uposx;
    private float uposy;
    private float uspeed;
    private float ususpAL;
    private float ususpAR;
    private float ususpFL;
    private float ususpFR;
    private float uwspAL;
    private float uwspAR;
    private float uwspFL;
    private float uwspFR;
    private float uthrottle;
    private float ubrakes;
    private float uclutch;
    private float usteering;
    private float ugear;
    private float ugForce_X;
    private float ugForce_Y;
    private float ucLap;
    private float urpm;

    public Packet(float utTime, float ulapTime, float udistance, float uposx, float uposy, float uspeed, float ususpAL, float ususpAR, float ususpFL, float ususpFR, float uwspAL, float uwspAR, float uwspFL, float uwspFR, float uthrottle, float ubrakes, float uclutch, float usteering, float ugear, float ugForce_X, float ugForce_Y, float ucLap, float urpm) {
        this.utTime = utTime;
        this.ulapTime = ulapTime;
        this.udistance = udistance;
        this.uposx = uposx;
        this.uposy = uposy;
        this.uspeed = uspeed;
        this.ususpAL = ususpAL;
        this.ususpAR = ususpAR;
        this.ususpFL = ususpFL;
        this.ususpFR = ususpFR;
        this.uwspAL = uwspAL;
        this.uwspAR = uwspAR;
        this.uwspFL = uwspFL;
        this.uwspFR = uwspFR;
        this.uthrottle = uthrottle;
        this.ubrakes = ubrakes;
        this.uclutch = uclutch;
        this.usteering = usteering;
        this.ugear = ugear;
        this.ugForce_X = ugForce_X;
        this.ugForce_Y = ugForce_Y;
        this.ucLap = ucLap;
        this.urpm = urpm;
    }

    public float getUtTime() {
        return utTime;
    }

    public float getUlapTime() {
        return ulapTime;
    }

    public float getUdistance() {
        return udistance;
    }

    public float getUposx() {
        return uposx;
    }

    public float getUposy() {
        return uposy;
    }

    public float getUspeed() {
        return uspeed;
    }

    public float getUsuspAL() {
        return ususpAL;
    }

    public float getUsuspAR() {
        return ususpAR;
    }

    public float getUsuspFL() {
        return ususpFL;
    }

    public float getUsuspFR() {
        return ususpFR;
    }

    public float getUwspAL() {
        return uwspAL;
    }

    public float getUwspAR() {
        return uwspAR;
    }

    public float getUwspFL() {
        return uwspFL;
    }

    public float getUwspFR() {
        return uwspFR;
    }

    public float getUthrottle() {
        return uthrottle;
    }

    public float getUbrakes() {
        return ubrakes;
    }

    public float getUclutch() {
        return uclutch;
    }

    public float getUsteering() {
        return usteering;
    }

    public float getUgear() {
        return ugear;
    }

    public float getUgForce_X() {
        return ugForce_X;
    }

    public float getUgForce_Y() {
        return ugForce_Y;
    }

    public float getUcLap() {
        return ucLap;
    }

    public float getUrpm() {
        return urpm;
    }
}
