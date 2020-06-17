package org.egbers.telemetry.dirt;

import controlP5.ControlP5;
import hypermedia.net.UDP;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.event.MouseEvent;

public class DashboardApplet extends PApplet {

    String ip="127.0.0.1";
    int portRX=10001;


    float gForceX = 0.0f;
    float gForceY = 0.0f;
    float steeringOutput = 0.0f;
    float fdiff = 0.0f;
    float cdiff = 0.0f;
    float rdiff = 0.0f;
    float maxSpd = 0.0f;

    ControlP5 cp5;
    PImage img;

    PacketHandler packetHandler;

//    @Override
//    public void mouseClicked(MouseEvent event) {
//        System.out.println(event);
//        cp5.getController("speed").setValue((float)Math.random()*200);
//        cp5.getController("gear").setValue((float)Math.random()*5);
//        cp5.getController("throttle").setValue((float)Math.random());
//    }

    @Override
    public void setup(){
        // Create new object for receiving
        packetHandler = new PacketHandler(this);
        UDP udp =new UDP(packetHandler, portRX, ip);
        udp.log(true);
        udp.listen(true);


        size(1920,200);
        smooth();
        background(0);

        img=loadImage("/Users/megbers/Documents/personal/workspaces/DirtRallyTelemetry/Reference/Zeb/src/main/resources/data/background1080_200revb.png");

        // Create some dials and gauges on screen
        cp5 = new ControlP5(this);
        // change the default font to Verdana
        PFont p = createFont("Georgia",14, true);
        PFont p1 = createFont("arial",24, true);
        PFont p2 = createFont("arial",96, true);
        //cp5.setControlFont(p);
        cp5.setFont(p);
        // change the original colors
        cp5.setColorForeground(0xffd0eff2);
        cp5.setColorBackground(0xff2e2e2e);
        //cp5.setColorLabel(0xffebf8ff);  //0a0a0a will hide it
        //cp5.setColorValue(0xffebf8ff);
        cp5.setColorValueLabel(0xffebf8ff);
        cp5.setColorActive(0xff12acdb);

        cp5.addSlider("rpm")
                .setSize(25,150)
                .setPosition(1033,14)
                .setValue(0)
                .setDecimalPrecision(0)
                .setRange(0,10000);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);

        cp5.addSlider("brakes")
                .setSize(25,150)
                .setPosition(800,14)
                .setValue(0)
                .setRange(0,1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("clutch")
                .setSize(25,150)
                .setPosition(875,14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0,1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);

        cp5.addSlider("throttle")
                .setSize(25,150)
                .setPosition(949,14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0,1);
        cp5.getController("throttle").getValueLabel().setVisible(false);
        cp5.getController("throttle").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("gear")
                .setSize(0,0)
                .setDecimalPrecision(0)
                .setPosition(575,110)
                .setValue(0);
        cp5.getController("gear").getCaptionLabel().setVisible(false);
        cp5.getController("gear").getValueLabel().setFont(p2);

        cp5.addNumberbox("lapTime")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setPosition(78,23)
                .setValue(0);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("distance")
                .setSize(75,25)
                .setDecimalPrecision(1)
                .setPosition(146,57)
                .setValue(0);
        cp5.getController("distance").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("posx")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(78,120)
                .setValue(0);

        cp5.addNumberbox("posy")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(78,150)
                .setValue(0);
        cp5.getController("posy").getCaptionLabel().setVisible(false);

        cp5.addKnob("speed")
                .setRadius(60)
                .setPosition(285,40)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0,280);
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

//Steering wheel
//cp5.addKnob("steering")
//               .setRange(-1,1)
//               .setValue(0)
//               .setPosition(1181,30)
//               .setRadius(70)
//               .setViewStyle(Knob.ELLIPSE)
//               .setNumberOfTickMarks(10)
//               .setTickMarkLength(4)
//               .snapToTickMarks(false)
//               .setColorForeground(color(255,255,240))
//               .setColorBackground(color(0,15,255,0))
//               .setColorActive(color(255,255,240))
//               .setDragDirection(Knob.HORIZONTAL)
//               ;
//        cp5.getController("steering").getCaptionLabel().setVisible(false);
//        cp5.getController("steering").getValueLabel().setVisible(false);

        cp5.addSlider("suspFL")
                .setSize(15,50)
                .setPosition(1460,29)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspFL").getValueLabel().setVisible(false);
        cp5.getController("suspFL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFR")
                .setSize(15,50)
                .setPosition(1545,29)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspFR").getValueLabel().setVisible(false);
        cp5.getController("suspFR").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAL")
                .setSize(15,50)
                .setPosition(1455,125)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspAL").getValueLabel().setVisible(false);
        cp5.getController("suspAL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAR")
                .setSize(15,50)
                .setPosition(1552,125)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspAR").getValueLabel().setVisible(false);
        cp5.getController("suspAR").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("maxSpd")
                .setSize(60,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(1848,87)
                .setValue(0);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("avgSpd")
                .setSize(60,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(1848,125)
                .setValue(0);
        cp5.getController("avgSpd").getCaptionLabel().setVisible(false);
    }

    @Override
    public void draw(){
        background(0);
        image(img,0,0);
        //GForces
        ellipseMode(CENTER);
        stroke(0);
        fill(255);
        ellipse(gForceX + 1251,gForceY + 100,16,16);
        //Steering
        ellipseMode(CENTER);
        stroke(0);
        fill(255);
        ellipse(steeringOutput + 1251,20,16,16);
        //Front Differential draw
        rectMode(CENTER);
        stroke(0);
        fill(255);
        rect(1511+fdiff,52,10,30);
        rect(1511,100+cdiff,30,10);
        rect(1511+rdiff,151,10,30);
    }

    public void gameDataOutput(float tTime, float lapTime, float speed, float gear, float cLap, float rpm, float wspFL){
        println("Total time: " + tTime);
        println("Lap time: " + lapTime);
        println("Speed: " + speed);
        println("Gear: " + gear);
        println("Current lap: " + cLap);
        println("RPM: " + rpm);
        println("SuspFL: " + wspFL);
    }

    // Function that outputs all the received game data
    public void fullOutput(byte[] data){
        // Loop all the received bytes
        for(int i=0; i <= data.length-1; i++){
            // Values consist of 4 bytes
            if(i % 4 == 0){
                // Combine 4 bytes to the value
                float val = Float.intBitsToFloat((data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | ((data[i+3] & 0xff) << 24));
                // Output the 'raw' value
                println("Value received at position " + i + " = " + val);
            }
        }
    }

    public void setControllerValue(String controller, float value) {
        cp5.getController(controller).setValue(value);
    }

    public void setMaxSpeed(float speed) {
        if(speed > maxSpd) {
            this.maxSpd = speed;
        }
        setControllerValue("maxSpd", maxSpd);
    }

    public void setGForceX(float gForceX) {
        this.gForceX = gForceX * 10;
        constrain(gForceX, -20, 20);
    }

    public void setGForceY(float gForceY) {
        this.gForceY = gForceY * 10;
        constrain(gForceY, -20, 20);
    }

    public void setSteeringOutput(float steeringOutput) {
        this.steeringOutput = steeringOutput * 70;
    }

    public void setFdiff(float wspFL, float wspFR) {
        this.fdiff = (wspFL*-1) + wspFR;
    }

    public void setCdiff(float wspFL, float wspFR, float wspAL, float wspAR) {
        this.cdiff = ((wspFL + wspFR)*-1) + (wspAL + wspAR);
    }

    public void setRdiff(float wspAL, float wspAR) {
        this.rdiff = (wspAL*-1) + wspAR;
    }
}
